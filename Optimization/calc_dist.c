/*
 * PROJ1-1: YOUR TASK A CODE HERE
 *
 * You MUST implement the calc_min_dist() function in this file.
 *
 * You do not need to implement/use the swap(), flip_horizontal(), transpose(), or rotate_ccw_90()
 * functions, but you may find them useful. Feel free to define additional helper functions.
 */

#include <stdlib.h>
#include <stdio.h>
#include <float.h>
#include <omp.h>
#include "digit_rec.h"
#include "utils.h"
#include "limits.h"
#include <time.h>
#include <emmintrin.h>

/* Returns correct image index based on shifts. */
unsigned int imageindex(int i_width, int i_height, int t_width, int exht, int exwt, int index);

/* Swaps the values pointed to by the pointers X and Y. */
void swap(float *x, float *y) {
    float tmp = *x;
    *x = *y;
    *y = tmp;
}

/* Flips the elements of a square array ARR across the y-axis. */
void flip_horizontal(float *arr, int width) {
    //int x, y;
    #pragma omp parallel 
    {
        #pragma omp for collapse(2)
        for (int y = 0; y < width * width; y=y+width) {
            for (int x = 0; x < width / 2; x=x+1) {
                swap(arr + y + x, arr + y + width - x - 1);
            }
        }
    }
}

/* Transposes the square array ARR. */
void transpose(float *arr, int width) {
    //int x, y;
    //intel load/store stuff
    #pragma omp parallel 
    {
        //int x, y;
        #pragma omp for
        for (int y = 0; y < width; y=y+1) {
            for (int x = 0; x < y; x=x+1) {
                swap(arr + width * y + x, arr + width * x + y);
            }
        }
    }
}

/* Rotates the square array ARR by 90 degrees counterclockwise. */
void rotate_ccw_90(float *arr, int width) {
    flip_horizontal(arr, width);
    transpose(arr, width);
  }

/* Returns correct image index based on shifts. */
unsigned int imageindex(int i_width, int i_height, int t_width, int exht, int exwt, int index) {
    int c = (index % t_width);
    int r = (index - c) / t_width;
    r+= exht;
    c+= exwt;
    int i = c + (r * i_width);

    return i;
}


/* Returns the squared Euclidean distance between TEMPLATE and IMAGE. The size of IMAGE
 * is I_WIDTH * I_HEIGHT, while TEMPLATE is square with side length T_WIDTH. The template
 * image should be flipped, rotated, and translated across IMAGE.
 */
float calc_min_dist(float *image, int i_width, int i_height, 
			     float *template, int t_width) {
    float min_dist = FLT_MAX;

    clock_t begin, end;
    double time_spent;
    begin = clock();
    #pragma omp parallel 
    {
    #pragma omp for collapse(3) 
    for (int exht = 0; exht < (i_height - t_width + 1); exht=exht+1) {

        for (int exwt = 0; exwt < (i_width - t_width + 1); exwt=exwt+1) {

    	    for (int rot = 0; rot < 4; rot=rot+1) {

        		for (int flip = 0; flip < 2; flip=flip+1) {
        		    
                        float accum = 0;
            		    for (unsigned int index = 0; index < (t_width * t_width); index=index+1) {

                			unsigned int image_index =
                			    imageindex(i_width, i_height, t_width, exht, exwt, index);
                			float diff = (template[index] - image[image_index]);
                			accum += diff * diff;
            		    
                        }



                        // float sum_l[4] = {0.0,0.0,0.0,0.0};
                        // __m128d accum = _mm_set_ps(0.0,0.0,0.0,0.0);
                        // for (unsigned int index = 0; index < (t_width * t_width)/16; index=index+4) {

                        //     unsigned int image_index =
                        //         imageindex(i_width, i_height, t_width, exht, exwt, index);
                        //     float diff = (template[index] - image[image_index]);

                        //     accum = _mm_add_epd32(accum, _mm_loadu_sd128(diff*diff));
                        //     //accum += diff * diff;
                        
                        // }

        		    if (accum < min_dist) { min_dist = accum;}
                    
        		    flip_horizontal(template, t_width);
        		}
    		
    		rotate_ccw_90(template, t_width);
    	    
            }
    	}
    }
    }

    end = clock();
    time_spent = (double) (end - begin) / CLOCKS_PER_SEC;
    printf("time spent: %f\n\n", time_spent);
    return min_dist;
}   
