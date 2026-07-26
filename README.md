# mog-frontend

[![CI](https://github.com/mogproject/mog-frontend/actions/workflows/ci.yml/badge.svg)](https://github.com/mogproject/mog-frontend/actions/workflows/ci.yml)

Common frontend components of Shogi Playground

# Testing

```
npm install jsdom
npm install uglify-js
npm install uglifycss
make test
```

### Java home for sbt

Set the Java home for sbt in `.sbtopts` in the project root. sbt reads this file automatically, so you do not need to pass `-java-home` from the `Makefile`.

```
-java-home
/Users/master/Library/Java/JavaVirtualMachines/openjdk-19.0.1/Contents/Home
```

If you use a different JDK, replace the path above with the Java home directory for your environment.

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

Or if you are using `tmux`, run `./scripts/tmux_start.sh`.


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

### GitHub Actions

GitHub Actions runs the build and test suite automatically on pushes and pull requests.

