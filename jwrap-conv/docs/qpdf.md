# [qpdf](http://qpdf.sourceforge.net/)

QPDF is a command-line program that does structural, content-preserving transformations on PDF files. It could have been
called something like pdf-to-pdf

```bash
brew install qpdf
sudo apt-get install -y qpdf
```

```bash
qpdf --decrypt input.pdf output.pdf
qpdf --password=$PASSWORD --decrypt input.pdf output.pdf
qpdf --show-encryption input.pdf
qpdf --show-encryption output.pdf

```
