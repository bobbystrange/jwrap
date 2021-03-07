# [Poppler](https://poppler.freedesktop.org/)

Poppler is a PDF rendering library based on the xpdf-3.0 code base.

```bash
sudo apt-get install xpdf-utils

```

### pdfimages

```bash
# -f <int>  : first page to convert
# -l <int>  : last page to convert
# -list     : print list of images instead of saving
pdfimages -list -f 1 -l 1 input.pdf
```

### pdftops

```bash
pdftops -upw $PASSWORD input.pdf


```

```bash
# The syntax is explained in the man page.
# Here we have R for rotate right,
# @1.2 to scale, (x,y) to move the result.
# The comma (,) produces 2 pages from each original page.
#
# Note that this will double the size of the resulting pdf,
# since each page is fully drawn twice,
# even though you only see half of it each time.

pstops -p a4 '0R@1.2(1cm,29cm),0R@1.2(-16cm,29cm)' input.ps output.ps
```
