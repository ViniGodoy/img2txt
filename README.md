# img2txt
Converts an image to an ascii art text. This code was created for my image processing classes at 
[PUCPR](http://www.pucpr.br). That's why grayscale conversion and shrinking are manually implemented.

This code uses two different processes:

## Method 1: Luminosity mapping ##
1. Create a character palette based in char luminance (see makePallete method)
1. Shrink the image with 2x4 slots
1. Convert to 10 shades of gray with dither
1. Map them to the character pallete

## Method 2: Shape mapping ##
1. Shrink the image to with 1x2 slots
1. Binarize the imagem with dithering
1. Analyze the shape of a 2x2 pixel slot
1. Map to the closest letter format


See [toText1.txt](https://github.com/ViniGodoy/img2txt/blob/master/toText1.txt) and [toText2.txt](https://github.com/ViniGodoy/img2txt/blob/master/toText2.txt) for results after processing [images/jobs.jpg](https://github.com/ViniGodoy/img2txt/blob/master/images/jobs.jpg)

