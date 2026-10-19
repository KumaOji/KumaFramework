# Kuma Console artwork

These illustrations are preserved historical versions. The active Yuna-inspired workspace set and its prompt records are in `yuna/ARTWORK.md`.

## Cute revision

- Previous asset: `kuma-lab-cute-v2.png` (1536 × 1024 PNG), saved in `renderer/assets/`.
- Generated with the built-in `image_gen` tool on 2026-10-19. A specific Image2 model could not be selected through this tool; no Image2 API key was configured. The original illustration is preserved.
- Used by the experiment finder header, Lab and overview banners, sidebar companion, native splash and browser welcome animation.

Final generation prompt:

> Use case: stylized-concept. Asset type: polished cute anime illustration for Kuma Console experiment finder banner and startup page. Original adorable chibi adult anime lab assistant, oversized expressive lavender eyes, soft round face, delighted friendly smile, fluffy pastel lavender twin-tail hair, tiny white bear hair clips, white and lilac modest lab-inspired outfit with pastel pink ribbon, holding a little glowing magnifying glass, tiny cute bear robot beside her. Sweet kawaii Japanese anime sticker-book style with beautiful clean linework, soft high quality cel shading, cozy candy pink, lavender, cream and pale sky blue palette. Whimsical miniature laboratory with softly glowing glass bottles, floating stars, hearts and little planet motifs. Wide landscape 3:2 composition: character and bear on RIGHT THIRD, generous almost empty pastel lavender/cream negative space on LEFT HALF for UI title and search overlay supplied in HTML. Charming and joyful, soft daylight, highly legible silhouette at small sizes. No text, no letters, no watermark, no logo, no UI screenshot, no border. Fully clothed, no sexualization.

## Original illustration

- Asset: `kuma-lab-anime.png` (1536 × 1024 PNG).
- Generated with the built-in `image_gen` tool on 2026-10-19. No CLI or external API runner was used. The built-in tool does not expose a model-selection parameter.
- Used by: native Electron splash, browser welcome animation, overview and Lab banners, sidebar companion card.
- Packaging: Gradle embeds `renderer/` into the console JAR; Electron includes `renderer/assets/` so the native splash can load its illustration before the backend starts. No remote image host is required.

Final generation prompt:

> Use case: stylized-concept. Asset type: production artwork for Kuma Console desktop developer app, startup splash and wide Lab hero banner. Create an exceptionally polished original anime illustration in a wide landscape 3:2 composition. An adult anime girl software researcher with flowing silver-lavender hair, bright violet eyes, small tasteful bear-shaped hair clips, elegant navy jacket over white blouse, sitting at a luminous futuristic lab desk with floating translucent abstract interface panels, tiny bear companion robot. Sophisticated Japanese anime game key visual, exquisite linework, soft cel shading, atmospheric depth, sparkling cyan and lavender lab lighting, midnight blue background with pink cherry blossom accents. Character occupies RIGHT HALF, keep LEFT HALF mostly open dark navy with subtle stars and flowing light trails for readable application copy added by code. Friendly, optimistic, calm, professional and fully clothed. No text, no letters, no logos, no watermark, no UI screenshot. High quality beautiful illustration.
