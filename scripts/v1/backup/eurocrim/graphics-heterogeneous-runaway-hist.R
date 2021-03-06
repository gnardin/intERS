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
en <- 10
to <- 40
ex <- 20
pu <- 40

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

nLL <- eLL[1:iLL-1][numReceivedRetaliation > 0,]
nLH <- eLH[1:iLH-1][numReceivedRetaliation > 0,]
nHL <- eHL[1:iHL-1][numReceivedRetaliation > 0,]
nHH <- eHH[1:iHH-1][numReceivedRetaliation > 0,]

data <- data.table(type="LL",
                   value=mean((nLL$numReceivedRetaliation-nLL$numCounterattack)/nLL$numReceivedRetaliation))
data <- rbind(data, data.table(type="LH",
                               value=mean((nLH$numReceivedRetaliation-nLH$numCounterattack)/nLH$numReceivedRetaliation)))
data <- rbind(data, data.table(type="HL",
                               value=mean((nHL$numReceivedRetaliation-nHL$numCounterattack)/nHL$numReceivedRetaliation)))
data <- rbind(data, data.table(type="HH",
                               value=mean((nHH$numReceivedRetaliation-nHH$numCounterattack)/nHH$numReceivedRetaliation)))


ggplot(data, aes(factor(type, levels=c("LL","LH","HL","HH")),  y=value*100)) +
  ylim(0,100) +
  labs(title = "") + xlab("Extorter Policy") + ylab("% Run away") +
  geom_bar(width=0.5) +
  geom_text(aes(label=formatC(value*100, digits=3), vjust=0))