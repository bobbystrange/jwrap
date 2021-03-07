# [ghostscript](https://www.ghostscript.com/)

An interpreter for the PostScript language and for PDF.

```bash
brew install ghostscript
sudo apt-get install -y ghostscript
```

##### pdf to pdf

```bash
gs -q -dNOPAUSE -dBATCH -sDEVICE=pdfwrite \
    -sOutputFile=unencrypted.pdf \
    -c .setpdfwrite -f encrypted.pdf
```

##### pdf split

```bash
# FirstPage is 1
# output-001.pdf, ... output-005.pdf
gs -dBATCH -dNOPAUSE -sDEVICE=pdfwrite \
    -dFirstPage=1 -dLastPage=5 \
    -sOutputFile=output-%03d.pdf input.pdf
```

##### pdf to image

```bash
# convert a single page pdf
gs -sDevice=jpeg -sOutputFile=output.jpg input.pdf

# convert a multi-page pdf file 
# and generate separate png file for each page
gs -dBATCH -dNOPAUSE -sDevice=jpeg \
    -r144 \
    -sOutputFile=output-%03d.jpg input.pdf
```

### ps2pdf

```bash
ps2pdf input.ps output.pdf
```

### pdf2ps

```bash
pdf2ps input.pdf output.ps
```

