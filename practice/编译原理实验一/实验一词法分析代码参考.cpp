#include <stdio.h>
#include <ctype.h> 
#include <stdlib.h>
#include <string.h>
#define NULL 0
#include <iostream>
using namespace std;
FILE *fp;
char cbuffer;
char *key[8]={"if","else","for","while","do","return","break","continue"};
char *border[6]={",",";","{","}","(",")"};
char *arithmetic[4]={"+","-","*","/"};
char *relation[6]={"<","<=","=",">",">=","<>"};
char *consts[20];
char *label[20];
int constnum=0,labelnum=0;/////////////////////////////////////////////////////////////////////////////////////////////////////
int search(char searchchar[],int wordtype)
{
	int i=0;
	switch (wordtype) {
	case 1:{
			for (i=0;i<=7;i++){
				if (strcmp(key[i],searchchar)==0)
					return(i+1);
		       }
			return 0;
		   }
	case 2:{
			for (i=0;i<=5;i++)
			  {
				if (strcmp(border[i],searchchar)==0)
					return(i+1);
			  }	       
			return(0);
		  }
	case 3:{
			for (i=0;i<=3;i++)
			{
				if (strcmp(arithmetic[i],searchchar)==0){
					return(i+1);
				}
			 }
			return(0);
		  }
	case 4:{
			for (i=0;i<=5;i++)
			  {
				if(strcmp(relation[i],searchchar)==0)
				{
					return(i+1);
				}
			  }
			return(0);
		  }
	case 5:{
			for (i=0;i<=constnum;i++)	
			  {
				if(i!=constnum)
				{
					if (strcmp(consts[i],searchchar)==0)
						return(i+1);
				}
			 }
			consts[i-1]=(char *)malloc(sizeof(searchchar));
			strcpy(consts[i-1],searchchar);
			constnum++;
			return(i);
		}
	case 6:{
			for (i=0;i<=labelnum;i++)
			  {
				if(i!=labelnum)
				{
					if(strcmp(label[i],searchchar)==0)
						return(i+1);
				}
			}
			label[i-1]=(char *)malloc(sizeof(searchchar));
			strcpy(label[i-1],searchchar);		
			labelnum++;
			return(i);
		}
	default:
		return 0;
	}
}/////////////////////////////////////////////////////////////////////////////////////////////////////
char alphaprocess(char buffer)
{
	int atype;
    int i=-1;
    char alphatp[20];
    while ((isalpha(buffer))||(isdigit(buffer)))
	{
		alphatp[++i]=buffer;
	    buffer=fgetc(fp);
	}
    alphatp[i+1]='\0';
    if (atype=search(alphatp,1))
	{
		printf("%8s  (1,%d)\n",alphatp,atype-1);
	}
    else
	{
		atype=search(alphatp,6);
		printf("%8s  (6,%d)\n",alphatp,atype-1);
	}
    return(buffer);
}/////////////////////////////////////////////////////////////////////////////////////////////////////
char digitprocess(char buffer)
{
	int i=-1;
    char digittp[20];
    int dtype;
    while ((isdigit(buffer)))
	{
		digittp[++i]=buffer;
	    buffer=fgetc(fp);
	}
    digittp[i+1]='\0';
    dtype=search(digittp,5);
    printf("%8s  (5,%d)\n",digittp,dtype-1);
    return(buffer);
}/////////////////////////////////////////////////////////////////////////////////////////////////////
char otherprocess(char buffer)
{
	int i=-1;
    char othertp[20];
    int otype,otypetp;
    othertp[0]=buffer;
    othertp[1]='\0';
    if (otype=search(othertp,3))
	{
		printf("%8s  (3,%d)\n",othertp,otype-1);
		buffer=fgetc(fp);
		goto out;
	}

	if(otype=search(othertp,4))
	{
		buffer=fgetc(fp);
	    othertp[1]=buffer;
	    othertp[2]='\0';
	    if (otypetp=search(othertp,4)){
			buffer=fgetc(fp);
		    }
	    else{
			othertp[1]='\0';
		    }
		printf("%8s  (4,%d)\n",othertp,otype-1);
		goto out;
	}

	if (buffer==':'){
		buffer=fgetc(fp);
		if (buffer=='=')
			printf("%8s  (2,2)\n",":=");
		buffer=fgetc(fp);
		goto out;
	 }
	else{
		if (otype=search(othertp,2))
		{
			printf("%8s  (2,%d)\n",othertp,otype-1);
			buffer=fgetc(fp);
			goto out;
		}
	}
	if ((buffer!='\n')&&(buffer!=' ')&&(buffer!='\t'))
	{
		printf("%8c  error,not a word\n",buffer);
    } 
	buffer=fgetc(fp);
out:  return(buffer);
}/////////////////////////////////////////////////////////////////////////////////////////////////////
void main()
{
	int i;
	for (i=0;i<=20;i++)
	{
		label[i]=NULL;
	    consts[i]=NULL;
	}
	if ((fp=fopen("example.c","r"))==NULL)
		printf("error!\n");
	else
	{
		cbuffer = fgetc(fp);
		while(cbuffer!=EOF)
		{
			if (isalpha(cbuffer))
				cbuffer=alphaprocess(cbuffer);	
			else if (isdigit(cbuffer))
				cbuffer=digitprocess(cbuffer);
			else 
				cbuffer=otherprocess(cbuffer);
		}
		printf("over!\n");
		system("pause");
	}
}
