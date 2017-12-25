# mog-frontend
Common frontend components of Shogi Playground

# Testing

```
npm install jsdom
make test
```

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
- Cut each piece to 43x48 format
- Add alpha channel and save as PNG images (manual adjustment required)
- Use www.aconvert.com to convert PNG files to SVG

