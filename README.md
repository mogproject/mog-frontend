# mog-frontend
Common frontend components of Shogi Playground

# Testing

```
npm install jsdom
make test
```

### Local Testing

- Terminal 1:

```
sbt
> ~test:fastOptJS
```

- Terminal 2:

```
make server
```

- Terminal 3:

```
make local
```

### Mobile Testing

```
make publish-commit
```

- Merge changes into `master` branch on Git (a merge request required)
- Access to `https://mogproject.github.io/index-debug.html?debug=true` on a mobile device



# How to Make Piece Image Files

### Alphabetic Pieces

- Download `ipaexm.ttf`
- Use Convertio to make an SVG file
- Find unicode for each character you want
- Traverse SVG file as a text file and copy path information for a specific character
- Manually resize or combile characters if needed

### Graphical Pieces

- Download bitmap images from http://shineleckoma.web.fc2.com/
  - License: Creative Commons
- Work on Gimp
  - Add alpha channel
  - Manually define polygonal paths for pieces
  - Set all paths visible and merge them
  - Create selection from merged paths, invert, and delete the selection
  - Expand canvas size
    - Width: 344 -> 430, Height: 288 -> 336, Offset: (43, 48)
  - Stroke paths: black, 2px
  - Export to a PNG file
- Cut each piece to 43x48 format
  - Use ImageMagick and this script: `scripts/extract_pieces.sh`
  - Usage: `extract_pieces.sh <large_image>.png`
- Use www.aconvert.com to convert PNG files to SVG

