set term qt size 400,400
set xlabel "Time"
set ylabel "Difference"
set title "Max temperature difference"
plot "max_difference_graphic_data.txt" using 1:2 with lines t ""
pause mouse close "Click to quit"