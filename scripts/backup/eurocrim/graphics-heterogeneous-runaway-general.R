###
### Required libraries
###
library(data.table)
library(ggplot2)
library(gridExtra)
library(scatterplot3d)

###
### Raw simulation data output directory
###
basePath <- "/data/workspace/gloders/intERS/output"

en <- 10
to <- 40

numRunawayLL <- matrix(0,nrow=4,ncol=5)
numRunawayLH <- matrix(0,nrow=4,ncol=5)
numRunawayHL <- matrix(0,nrow=4,ncol=5)
numRunawayHH <- matrix(0,nrow=4,ncol=5)

for(ex in 1:5){
  for(pu in 2:5){
    
    outputPath <- paste(basePath,"/En",en,"_To",to,
                        "/Ex",(ex*10),"-",(ex*20),"_Pu",(pu*10),"-",(pu*20),"_En",en,"_To",to,
                        "/LL-LH-HL-HH", sep="")
    
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
    
    eLL <- extorterSum[which(type == 0),]
    eLH <- extorterSum[which(type == 1),]
    eHL <- extorterSum[which(type == 2),]
    eHH <- extorterSum[which(type == 3),]
    
    numRunawayLL[pu-1,ex] <- mean(eLL[1:50,][which(numReceivedRetaliation > 0),
                                 (numReceivedRetaliation-numCounterattack)])
    numRunawayLH[pu-1,ex] <- mean(eLH[1:50,][which(numReceivedRetaliation > 0),
                                             (numReceivedRetaliation-numCounterattack)])
    numRunawayHL[pu-1,ex] <- mean(eHL[1:50,][which(numReceivedRetaliation > 0),
                                             (numReceivedRetaliation-numCounterattack)])
    numRunawayHH[pu-1,ex] <- mean(eHH[1:50,][which(numReceivedRetaliation > 0),
                                             (numReceivedRetaliation-numCounterattack)])
  }
}

dimnames(numRunawayLL) <- list(c("[20|40]","[30,60]","[40,80]","[50,100]"),
                               c("[10|20]","[20,40]","[30,60]","[40,80]","[50,100]"))
dimnames(numRunawayLH) <- list(c("[20|40]","[30,60]","[40,80]","[50,100]"),
                               c("[10|20]","[20,40]","[30,60]","[40,80]","[50,100]"))
dimnames(numRunawayHL) <- list(c("[20|40]","[30,60]","[40,80]","[50,100]"),
                               c("[10|20]","[20,40]","[30,60]","[40,80]","[50,100]"))
dimnames(numRunawayHH) <- list(c("[20|40]","[30,60]","[40,80]","[50,100]"),
                               c("[10|20]","[20,40]","[30,60]","[40,80]","[50,100]"))

xLL <- data.frame(extortion=c(col(numRunawayLL)),
                  punishment=c(row(numRunawayLL)), value=c(numRunawayLL))
xLH <- data.frame(extortion=c(col(numRunawayLH)),
                  punishment=c(row(numRunawayLH)), value=c(numRunawayLH))
xHL <- data.frame(extortion=c(col(numRunawayHL)),
                  punishment=c(row(numRunawayHL)), value=c(numRunawayHL))
xHH <- data.frame(extortion=c(col(numRunawayHH)),
                  punishment=c(row(numRunawayHH)), value=c(numRunawayHH))

sLL <- scatterplot3d(xLL, type = "h", lwd = 5, pch=" ", box=FALSE, angle=60,
                     xlab="Extortion", ylab="Punishment", zlab="Number of Runaway (LL)",
                     x.ticklabs=colnames(numRunawayLL),
                     y.ticklabs=c("[20,40]","","[30,60]","","[40,80]","","[50,100]"),
                     z.ticklabs="",
                     color = grey(20:1 / 40), main="")
my.lm <- lm(xLL$value ~ xLL$extortion + xLL$punishment)
sLL$plane3d(my.lm)

sLH <- scatterplot3d(xLH, type = "h", lwd = 5, pch=" ", box=FALSE, angle=60,
                     xlab="Extortion", ylab="Punishment", zlab="Number of Runaway (LH)",
                     x.ticklabs=colnames(numRunawayLH),
                     y.ticklabs=c("[20,40]","","[30,60]","","[40,80]","","[50,100]"),
                     z.ticklabs="",
                     color = grey(20:1 / 40), main="")
my.lm <- lm(xLH$value ~ xLH$extortion + xLH$punishment)
sLH$plane3d(my.lm)

sHL <- scatterplot3d(xHL, type = "h", lwd = 5, pch=" ", box=FALSE, angle=60,
                     xlab="Extortion", ylab="Punishment", zlab="Number of Runaway (HL)",
                     x.ticklabs=colnames(numRunawayHL),
                     y.ticklabs=c("[20,40]","","[30,60]","","[40,80]","","[50,100]"),
                     z.ticklabs="",
                     color = grey(20:1 / 40), main="")
my.lm <- lm(xHL$value ~ xHL$extortion + xHL$punishment)
sHL$plane3d(my.lm)

sHH <- scatterplot3d(xHH, type = "h", lwd = 5, pch=" ", box=FALSE, angle=60,
                     xlab="Extortion", ylab="Punishment", zlab="Number of Runaway (HH)",
                     x.ticklabs=colnames(numRunawayHH),
                     y.ticklabs=c("[20,40]","","[30,60]","","[40,80]","","[50,100]"),
                     z.ticklabs="",
                     color = grey(20:1 / 40), main="")
my.lm <- lm(xHH$value ~ xHH$extortion + xHH$punishment)
sHH$plane3d(my.lm)