# pdftk

```bash
sudo apt-get install -y pdftk
```

##### pdf to pdf

```bash
pdftk input.pdf output output.pdf user_pw $PASSWORD
pdftk input.pdf output output.pdf user_pw $PASSWORD owner_pw $PASSWORD
pdftk input.pdf output output.pdf input_pw $PASSWORD
```

##### pdf split

```bash
# it will split each page in your pdf file ,
# into a new pdf file and report info on doc_data.txt
# doc_data.txt, pg_0001.pdf, pg_0002.pdf, ...
pdftk input.pdf burst
```
