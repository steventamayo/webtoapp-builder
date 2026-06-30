import os
import io
import base64
from PIL import Image, ImageDraw

SIZES = [
    ('mipmap-mdpi', 48),
    ('mipmap-hdpi', 72),
    ('mipmap-xhdpi', 96),
    ('mipmap-xxhdpi', 144),
    ('mipmap-xxxhdpi', 192),
]

raw = os.environ.get('ICON_B64', '').strip()
base_img = None

if raw:
    try:
        data = raw.split(',')[-1]
        rem = len(data) % 4
        if rem:
            data += '=' * (4 - rem)
        base_img = Image.open(io.BytesIO(base64.b64decode(data))).convert('RGBA')
        print('Custom icon loaded OK')
    except Exception as e:
        print(f'Custom icon error: {e} — using default')

for folder, size in SIZES:
    os.makedirs(f'app/src/main/res/{folder}', exist_ok=True)
    if base_img is not None:
        img = base_img.resize((size, size), Image.LANCZOS)
    else:
        img = Image.new('RGBA', (size, size), (255, 77, 28, 255))
        d = ImageDraw.Draw(img)
        p = size // 5
        d.rectangle(
            [p, p, size - p, size - p],
            outline=(255, 255, 255, 200),
            width=max(2, size // 18),
        )
    img.save(f'app/src/main/res/{folder}/ic_launcher.png')
    img.save(f'app/src/main/res/{folder}/ic_launcher_round.png')

print(f'Icons OK for {len(SIZES)} densities')
