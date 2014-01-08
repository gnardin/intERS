###
### Required libraries
###
library(data.table)
library(ggplot2)
library(gridExtra)

###
### Command Arguments
###
args <- commandArgs(TRUE)

###
### Raw simulation data output directory
###
en <- as.numeric(args[1])
to <- as.numeric(args[2])
ex <- as.numeric(args[3])
pu <- as.numeric(args[4])

###
### Comment all those lines when using with Rscript
###
#rm(list=ls())
#en <- 10
#to <- 40
#ex <- 10
#pu <- 40

###
### Raw simulation data output directory
###
basePath <- "/data/workspace/gloders/intERS/output"

outputPath <- paste(basePath,"/En",en,"_To",to,
                    "/Ex",ex,"-",(ex*2),"_Pu",pu,"-",(pu*2),"_En",en,"_To",to,
                    "/LL-LH-HL-HH", sep="")
        
###
### Images repository directory
###
dir.create(file.path(outputPath, "images"), showWarnings = FALSE)
imagePath <- paste(outputPath,"/images", sep="")

###
### Upload Consolidated Information
###
extorterSum <- data.table(read.csv(paste(outputPath,"/cSumextorters.csv",sep=""),sep=";"))
setkey(extorterSum, type, cycle)

extorterAvg <- data.table(read.csv(paste(outputPath,"/cAvgextorters.csv",sep=""),sep=";"))
setkey(extorterAvg, type, cycle)

observerSum <- data.table(read.csv(paste(outputPath,"/","cSumobservers.csv",sep=""), sep=";"))
setkey(observerSum, type, cycle)

observerAvg <- data.table(read.csv(paste(outputPath,"/","cAvgobservers.csv",sep=""), sep=";"))
setkey(observerAvg, type, cycle)

targetSum <- data.table(read.csv(paste(outputPath,"/","cSumtargets.csv",sep=""), sep=";"))
setkey(targetSum, type, cycle)

targetAvg <- data.table(read.csv(paste(outputPath,"/","cAvgtargets.csv",sep=""), sep=";"))
setkey(targetAvg, type, cycle)



###
### INDEX NUMBER OF EXTORTERS < 1
###
iLL <- as.numeric(min((1:NROW(observerAvg))[(observerAvg$FR0 +
                                               observerAvg$IM0) < 1],
                      NROW(observerAvg)+1))
iLH <- as.numeric(min((1:NROW(observerAvg))[(observerAvg$FR1 +
                                               observerAvg$IM1) < 1],
           NROW(observerAvg)+1))
iHL <- as.numeric(min((1:NROW(observerAvg))[(observerAvg$FR2 +
                                               observerAvg$IM2) < 1],
           NROW(observerAvg)+1))
iHH <- as.numeric(min((1:NROW(observerAvg))[(observerAvg$FR3 +
                                               observerAvg$IM3) < 1],
           NROW(observerAvg)+1))



###
### EXTORTER RUNAWAY
###
eLL <- extorterSum[which(type == 0),]
eLH <- extorterSum[which(type == 1),]
eHL <- extorterSum[which(type == 2),]
eHH <- extorterSum[which(type == 3),]

nLL <- eLL[,(numReceivedRetaliation-numCounterattack)/numExtortion]
nLL[is.nan(nLL)] <- 0

nLH <- eLH[,(numReceivedRetaliation-numCounterattack)/numExtortion]
nLH[is.nan(nLH)] <- 0

nHL <- eHL[,(numReceivedRetaliation-numCounterattack)/numExtortion]
nHL[is.nan(nHL)] <- 0

nHH <- eHH[,(numReceivedRetaliation-numCounterattack)/numExtortion]
nHH[is.nan(nHH)] <- 0

if(iLL <= NROW(nLL)){
  nLL[iLL:NROW(nLL)] <- 0
}
if(iLH <= NROW(nLH)){
  nLH[iLH:NROW(nLH)] <- 0
}
if(iHL <= NROW(nHL)){
  nHL[iHL:NROW(nHL)] <- 0
}
if(iHH <= NROW(nHH)){
  nHH[iHH:NROW(nHH)] <- 0
}

nLL <- data.table(cycle=eLL$cycle,value=nLL)
nLH <- data.table(cycle=eLH$cycle,value=nLH)
nHL <- data.table(cycle=eHL$cycle,value=nHL)
nHH <- data.table(cycle=eHH$cycle,value=nHH)

minRunawayY <- 0
maxRunawayY <- 1

plotLL <- ggplot(nLL[1:100], aes(cycle)) +
  ylim(minRunawayY,maxRunawayY) +
  labs(title = "LL") + xlab("") + ylab("%") +
  geom_line(aes(y=value))

plotLH <- ggplot(nLH[1:100], aes(cycle)) +
  ylim(minRunawayY,maxRunawayY) +
  labs(title = "LH") + xlab("") + ylab("%") +
  geom_line(aes(y=value))

plotHL <- ggplot(nHL[1:100], aes(cycle)) +
  ylim(minRunawayY,maxRunawayY) +
  labs(title = "HL") + xlab("") + ylab("%") +
  geom_line(aes(y=value))

plotHH <- ggplot(nHH[1:100], aes(cycle)) +
  ylim(minRunawayY,maxRunawayY) +
  labs(title = "HH") + xlab("") + ylab("%") +
  geom_line(aes(y=value))

png(file=paste(imagePath,"/numberRunawayXExtortion.png", sep=""),
    width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Number of Runaway X Extortion")
dev.off()



###
### Images repository directory
###
dir.create(file.path(basePath, "image_summary"), showWarnings = FALSE)
imagePath <- paste(basePath,"/image_summary", sep="")

ext <- paste("-Ex",ex,"-",(ex*2),"_Pu",pu,"-",(pu*2),"_En",en,"_To",to, sep="")

png(file=paste(imagePath,"/numberRunawayXExtortion",ext,".png", sep=""),
    width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Number of Runaway X Extortion")
dev.off()