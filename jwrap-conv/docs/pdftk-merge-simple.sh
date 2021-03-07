#!/usr/bin/env bash

# merge two pages to one page

# A simple case:
# Step One: Split into individual pages
pdftk clpdf.pdf burst
# Step Two: Create left and right half pages
pw=`cat doc_data.txt  | grep PageMediaDimensions | head -1 | awk '{print $2}'`
ph=`cat doc_data.txt  | grep PageMediaDimensions | head -1 | awk '{print $3}'`
w2=$(( pw / 2 ))
w2px=$(( w2*10 ))
hpx=$((  ph*10 ))
for f in  pg_[0-9]*.pdf ; do
    lf=left_$f
    rf=right_$f
    gs -o ${lf} -sDEVICE=pdfwrite -g${w2px}x${hpx} -c "<</PageOffset [0 0]>> setpagedevice" -f ${f}
    gs -o ${rf} -sDEVICE=pdfwrite -g${w2px}x${hpx} -c "<</PageOffset [-${w2} 0]>> setpagedevice" -f ${f}
done
# Step Three: Merge left and right in order to produce newfile.pdf containing single page .pdf.
ls -1 [lr]*_[0-9]*pdf | sort -n -k3 -t_ > fl
pdftk `cat fl`  cat output newfile.pdf
