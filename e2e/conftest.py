"""Shared fixtures for the Hero Code Day end-to-end tests.

By default the suite starts its own copy of the app on a free port and stops it afterwards, so it
never clashes with a website you already have running on port 8080.

To test an app that is already running instead (for example http://localhost:8080):

    E2E_BASE_URL=http://localhost:8080 pytest
"""

import base64
import os
import re
import signal
import socket
import subprocess
import time
import urllib.error
import urllib.request
from pathlib import Path

import pytest
from playwright.sync_api import APIRequestContext, Playwright
from pytest_html import extras

from pages import HeroApp

REPO_ROOT = Path(__file__).resolve().parent.parent
STARTUP_TIMEOUT_SECONDS = 180  # a cold Maven start can be slow
EXTERNAL_TIMEOUT_SECONDS = 15  # an app that should already be running either answers or it does not


def pytest_html_report_title(report):
    report.title = "Hero Code Day: end-to-end test report"


@pytest.hookimpl(hookwrapper=True)
def pytest_runtest_makereport(item, call):
    """When a browser test fails, embed a screenshot of the page in the HTML report (one file, no extras)."""
    outcome = yield
    report = outcome.get_result()
    if report.when != "call" or not report.failed:
        return
    page = item.funcargs.get("page")
    if page is None:  # API-only tests have no page
        return
    try:
        # JPEG keeps the single-file report small; full_page shows the whole ranking board.
        screenshot = page.screenshot(full_page=True, type="jpeg", quality=60)
    except Exception:  # the page may already be closed; the report is still useful without a picture
        return
    report.extras = [*getattr(report, "extras", []), extras.jpg(base64.b64encode(screenshot).decode())]


def _free_port() -> int:
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
        sock.bind(("127.0.0.1", 0))
        return sock.getsockname()[1]


def _wait_until_ready(url: str, server: subprocess.Popen | None, timeout: float) -> None:
    deadline = time.monotonic() + timeout
    while time.monotonic() < deadline:
        if server is not None and server.poll() is not None:
            raise RuntimeError(f"The app stopped during start-up (exit code {server.returncode}).")
        try:
            with urllib.request.urlopen(f"{url}/api/questions", timeout=2) as response:
                if response.status == 200:
                    return
        except (urllib.error.URLError, ConnectionError, TimeoutError):
            pass
        time.sleep(0.5)
    raise RuntimeError(f"The app did not become ready at {url} within {timeout:g}s.")


@pytest.fixture(scope="session")
def base_url(tmp_path_factory):
    """URL of the app under test: E2E_BASE_URL if set, otherwise a freshly started local copy."""
    external = os.environ.get("E2E_BASE_URL")
    if external:
        url = external.rstrip("/")
        try:
            _wait_until_ready(url, None, EXTERNAL_TIMEOUT_SECONDS)
        except RuntimeError as error:
            pytest.exit(f"E2E_BASE_URL is set but the app is not answering. {error}", returncode=2)
        yield url
        return

    port = _free_port()
    url = f"http://127.0.0.1:{port}"
    log_path = tmp_path_factory.mktemp("app") / "spring-boot.log"
    with open(log_path, "w") as log:
        # start_new_session puts Maven and the Java process it forks into one process group,
        # so we can stop both together at the end.
        server = subprocess.Popen(
            ["mvn", "-q", "spring-boot:run", f"-Dspring-boot.run.arguments=--server.port={port}"],
            cwd=REPO_ROOT,
            stdout=log,
            stderr=subprocess.STDOUT,
            start_new_session=True,
        )
        try:
            _wait_until_ready(url, server, STARTUP_TIMEOUT_SECONDS)
        except Exception:
            _stop(server)
            raise RuntimeError(f"Could not start the app. Log: {log_path}\n{log_path.read_text()[-3000:]}")
        try:
            yield url
        finally:
            _stop(server)


def _stop(server: subprocess.Popen) -> None:
    if server.poll() is None:
        os.killpg(os.getpgid(server.pid), signal.SIGTERM)
        try:
            server.wait(timeout=20)
        except subprocess.TimeoutExpired:
            os.killpg(os.getpgid(server.pid), signal.SIGKILL)


@pytest.fixture(scope="session")
def browser_context_args(browser_context_args):
    """Ask for reduced motion: style.css then switches its animations off, so Playwright never has to
    wait for moving elements to settle. Faster and less flaky; it does not change what the page does."""
    return {**browser_context_args, "reduced_motion": "reduce"}


@pytest.fixture(autouse=True)
def no_external_fonts(context):
    """The page loads Google Fonts. Answer with an empty stylesheet so tests run offline and fast."""
    context.route(
        re.compile(r"https://fonts\.(googleapis|gstatic)\.com/.*"),
        lambda route: route.fulfill(status=200, content_type="text/css", body=""),
    )


@pytest.fixture
def app(page, base_url) -> HeroApp:
    """The website, opened on its first screen."""
    hero_app = HeroApp(page, base_url)
    hero_app.open()
    return hero_app


@pytest.fixture(scope="session")
def api(playwright: Playwright, base_url) -> APIRequestContext:
    """A client for calling the JSON API directly."""
    context = playwright.request.new_context(base_url=base_url)
    yield context
    context.dispose()


@pytest.fixture(scope="session")
def api_heroes(api) -> list[dict]:
    """The ranking as the backend sends it: a list of {rank, hero, you}, without a player."""
    return api.get("/api/heroes").json()
