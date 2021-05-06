# [imagemagick](https://www.imagemagick.org/)

Use ImageMagick® to create, edit, compose, or convert bitmap images. It can read and write images in a variety of
formats (over 200)
including PNG, JPEG, GIF, HEIC, TIFF, DPX, EXR, WebP, Postscript, PDF, and SVG.

Use ImageMagick to resize, flip, mirror, rotate, distort, shear and transform images, adjust image colors, apply various
special effects, or draw text, lines, polygons, ellipses and Bézier curves.

```bash
brew install imagemagick
sudo apt-get install imagemagick
```

##### pdf to image

```bash
# generate demo-0.jpg, demo-1.jpg, ...
# -density, horizontal and vertical density of the image
# use a value of around 175 and the text should become clearer than before
convert -density 175 demo.pdf demo.jpg

# 0 means the first page and then increment 1 for each page.
# -thumbnail, create a thumbnail of the image
# -flatten, put a white background in the transparent areas.
convert -thumbnail x300 demo.pdf[2] -flatten demo.jpg

# -scale, scale the image
# -quality, JPEG/MIFF/PNG compression level
convert -scale x800 -quality 75 demo.pdf[0] demo.jpg

```

```bash
# create a gif animation of all the pages
# 100ms delay, 
convert -thumbnail x300 -delay 100 demo.pdf demo.gif

```

```bash
# demo-0000.jpg, demo-0001.jpg, ...
convert -resize 1000x1000 demo.pdf demo-%04d.jpg

convert file.pdf[5-10,15-22,35] images/image.png


```

### image to image

```bash
convert -crop 500x1000+500+0 input.jpg output.jpg

```
