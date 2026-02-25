#include <iostream>
#include <fstream>
#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include <string>
#include <cstring>

int getData(char f[], char carrier[]);

int main(){
    if((getData("Airline_Delay_Cause.csv", "AA")) == 1){

    }
}

int getData(char f[], char carrier[]){
    FILE *fpi;
    char c[1024];

    fpi = fopen(f, "r"); //Opens file and checks if it opens

    if(fpi == NULL){
        printf("Input file error\n");
        return 1;
    }

    while(fgets(c, sizeof(c), fpi)){    //gets line from csv

        char *t = strtok(c, ",");   //parse the line by commas

        while(t != NULL){
            if (strcmp(t, carrier) == 0){
            printf("%s ", t);
            }
            t = strtok(NULL, ",");
            
        }
        printf("\n");
    }
    printf("Finished\n");
    fclose(fpi);
    return(0);
}