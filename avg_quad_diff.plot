set term qt size 400,400
set xlabel "Time"
set ylabel "Difference"
set title "Average quad temperature difference"
plot "avg_quad_difference_graphic_data.txt" using 1:2 with lines t ""
pause mouse close "Click to quit"