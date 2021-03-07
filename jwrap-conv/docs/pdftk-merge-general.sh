#!/usr/bin/env bash

# merge two pages to one page

# Step One: split with pdftk as before

# Step Two: Now create three files that contain the width and height of each pages
# and a default for the fraction of the split the left page will use.
grep PageMediaDimensions <doc_data.txt | awk '{print $2}'    >   pws.txt
grep PageMediaDimensions <doc_data.txt | awk '{print $3}'    > phs.txt
grep PageMediaDimensions <doc_data.txt | awk '{print "0.5"}' > lfrac.txt
# the file lfrac.txt can be hand edited
# if information is available for where to split different pages.

# Step Three: Now create left and right split pages,
# using the different pages sizes
# and (if edited) different fractional locations for the split.
exec 3<pws.txt
exec 4<phs.txt
exec 5<lfrac.txt

for f in  pg_[0-9]*.pdf ; do
    read <&3 pwloc
    read <&4 phloc
    read <&5 lfr
    wl=`echo "($lfr)"'*'"$pwloc" | bc -l`;wl=`printf "%0.f" $wl`
    wr=$(( pwloc - wl ))
    lf=left_$f
    rf=right_$f
    hpx=$((  phloc*10 ))
    w2px=$(( wl*10 ))
    gs -o ${lf} -sDEVICE=pdfwrite -g${w2px}x${hpx} -c "<</PageOffset [0 0]>> setpagedevice" -f ${f}
    w2px=$(( wr*10 ))
    gs -o ${rf} -sDEVICE=pdfwrite -g${w2px}x${hpx} -c "<</PageOffset [-${wl} 0]>> setpagedevice" -f ${f}
done

# Step Four: This is the same merge step as in the previous, simpler, example.
ls -1 [lr]*_[0-9]*pdf | sort -n -k3 -t_ > fl
pdftk `cat fl`  cat output newfile.pdf
