# img2txt
Converts an image to an ascii art text

Uses two different processes:

## Method 1 ##
1. Create a character palette based in char luminance (see makePallete method)
1. Shrink the image with 2x4 slots
1. Convert to 10 shades of gray with dither
1. Map them to the character pallete

## Method 2 ##
1. Shrink the image to with 1x2 slots
1. Binarize the imagem with dithering
1. Analyze the shape of a 2x2 pixel slot
1. Map to the closest letter format

