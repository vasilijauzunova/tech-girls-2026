// Draws a hero picture as SVG, using the hero's two colors and emblem.
// You do not need to change anything in this file.

const EMBLEMS = {
  flame: '<path d="M12 2C13 7 18 9 18 15a6 6 0 0 1-12 0c0-3 2-4 3-6 1 2 2 2 2 0 0-3 1-7 1-7z"/>',
  shield: '<path d="M12 2l8 3v7c0 5-4 8-8 10-4-2-8-5-8-10V5z"/>',
  bolt: '<path d="M13 2L5 14h6l-1 8 8-12h-6z"/>',
  atom: '<circle cx="12" cy="12" r="2.2"/>' +
        '<g fill="none" stroke="white" stroke-width="1.6">' +
        '<ellipse cx="12" cy="12" rx="10" ry="4"/>' +
        '<ellipse cx="12" cy="12" rx="10" ry="4" transform="rotate(60 12 12)"/>' +
        '<ellipse cx="12" cy="12" rx="10" ry="4" transform="rotate(120 12 12)"/></g>',
  spark: '<path d="M12 2l2 8 8 2-8 2-2 8-2-8-8-2 8-2z"/>',
  star: '<path d="M12 2l3 7 7 .5-5.500 4.700L18.500 22 12 18l-6.500 4 2-7.800L2 9.500 9 9z"/>',
  code: '<g fill="none" stroke="white" stroke-width="2.600" stroke-linecap="round" stroke-linejoin="round">' +
        '<polyline points="8,6 2,12 8,18"/><polyline points="16,6 22,12 16,18"/></g>',
  heart: '<path d="M12 21C4 15 2 10 2 7a5 5 0 0 1 10-1 5 5 0 0 1 10 1c0 3-2 8-10 14z"/>',
  wave: '<path d="M2 12q4-7 8 0t8 0 4 0v8H2z"/>',
  moon: '<path d="M16 3a9 9 0 1 0 5 12A7 7 0 0 1 16 3z"/>',
  leaf: '<path d="M4 20C4 10 10 4 20 4c0 10-6 16-16 16z"/>'
};

let avatarCounter = 0;

function avatarSvg(hero, size) {
  const id = 'av' + (avatarCounter++);
  const emblem = EMBLEMS[hero.emblem] || EMBLEMS.star;
  return `
  <svg class="avatar" width="${size}" height="${size}" viewBox="0 0 100 100" role="img" aria-label="Picture of ${hero.name}">
    <defs>
      <linearGradient id="${id}" x1="0" y1="0" x2="1" y2="1">
        <stop offset="0" stop-color="${hero.colorA}"/>
        <stop offset="1" stop-color="${hero.colorB}"/>
      </linearGradient>
      <clipPath id="${id}c"><circle cx="50" cy="50" r="50"/></clipPath>
    </defs>
    <g clip-path="url(#${id}c)">
      <rect width="100" height="100" fill="url(#${id})"/>
      <circle cx="78" cy="22" r="26" fill="white" opacity="0.14"/>
      <path d="M14 100 L26 66 Q50 50 74 66 L86 100 Z" fill="#000" opacity="0.28"/>
      <path d="M22 100 Q22 70 50 66 Q78 70 78 100 Z" fill="#14122b"/>
      <circle cx="50" cy="42" r="17" fill="#14122b"/>
      <rect x="35" y="37" width="30" height="8" rx="4" fill="${hero.colorA}"/>
      <rect x="35" y="37" width="30" height="8" rx="4" fill="white" opacity="0.35"/>
      <g transform="translate(40 69) scale(0.83)" fill="white">${emblem}</g>
    </g>
  </svg>`;
}
