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

numFightLL <- matrix(0,nrow=4,ncol=5)
numFightLH <- matrix(0,nrow=4,ncol=5)
numFightHL <- matrix(0,nrow=4,ncol=5)
numFightHH <- matrix(0,nrow=4,ncol=5)

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
    
    numFightLL[pu-1,ex] <- mean(eLL[1:50,(numCounterattack+numReceivedCounterattack)])
    numFightLH[pu-1,ex] <- mean(eLH[1:50,(numCounterattack+numReceivedCounterattack)])
    numFightHL[pu-1,ex] <- mean(eHL[1:50,(numCounterattack+numReceivedCounterattack)])
    numFightHH[pu-1,ex] <- mean(eHH[1:50,(numCounterattack+numReceivedCounterattack)])
  }
}

dimnames(numFightLL) <- list(c("[20|40]","[30,60]","[40,80]","[50,100]"),
                                c("[10|20]","[20,40]","[30,60]","[40,80]","[50,100]"))
dimnames(numFightLH) <- list(c("[20|40]","[30,60]","[40,80]","[50,100]"),
                                c("[10|20]","[20,40]","[30,60]","[40,80]","[50,100]"))
dimnames(numFightHL) <- list(c("[20|40]","[30,60]","[40,80]","[50,100]"),
                                c("[10|20]","[20,40]","[30,60]","[40,80]","[50,100]"))
dimnames(numFightHH) <- list(c("[20|40]","[30,60]","[40,80]","[50,100]"),
                                c("[10|20]","[20,40]","[30,60]","[40,80]","[50,100]"))

xLL <- data.frame(extortion=c(col(numFightLL)),
                  punishment=c(row(numFightLL)), value=c(numFightLL))
xLH <- data.frame(extortion=c(col(numFightLH)),
                  punishment=c(row(numFightLH)), value=c(numFightLH))
xHL <- data.frame(extortion=c(col(numFightHL)),
                  punishment=c(row(numFightHL)), value=c(numFightHL))
xHH <- data.frame(extortion=c(col(numFightHH)),
                  punishment=c(row(numFightHH)), value=c(numFightHH))

sLL <- scatterplot3d(xLL, type = "h", lwd = 5, pch=" ", box=FALSE, angle=60,
                     xlab="Extortion", ylab="Punishment", zlab="Number of Fight (LL)",
                     x.ticklabs=colnames(numFightLL),
                     y.ticklabs=c("[20,40]","","[30,60]","","[40,80]","","[50,100]"),
                     z.ticklabs="",
                     color = grey(20:1 / 40), main="")
my.lm <- lm(xLL$value ~ xLL$extortion + xLL$punishment)
sLL$plane3d(my.lm)

sLH <- scatterplot3d(xLH, type = "h", lwd = 5, pch=" ", box=FALSE, angle=60,
                     xlab="Extortion", ylab="Punishment", zlab="Number of Fight (LH)",
                     x.ticklabs=colnames(numFightLH),
                     y.ticklabs=c("[20,40]","","[30,60]","","[40,80]","","[50,100]"),
                     z.ticklabs="",
                     color = grey(20:1 / 40), main="")
my.lm <- lm(xLH$value ~ xLH$extortion + xLH$punishment)
sLH$plane3d(my.lm)

sHL <- scatterplot3d(xHL, type = "h", lwd = 5, pch=" ", box=FALSE, angle=60,
                     xlab="Extortion", ylab="Punishment", zlab="Number of Fight (HL)",
                     x.ticklabs=colnames(numFightHL),
                     y.ticklabs=c("[20,40]","","[30,60]","","[40,80]","","[50,100]"),
                     z.ticklabs="",
                     color = grey(20:1 / 40), main="")
my.lm <- lm(xHL$value ~ xHL$extortion + xHL$punishment)
sHL$plane3d(my.lm)

sHH <- scatterplot3d(xHH, type = "h", lwd = 5, pch=" ", box=FALSE, angle=60,
                     xlab="Extortion", ylab="Punishment", zlab="Number of Fight (HH)",
                     x.ticklabs=colnames(numFightHH),
                     y.ticklabs=c("[20,40]","","[30,60]","","[40,80]","","[50,100]"),
                     z.ticklabs="",
                     color = grey(20:1 / 40), main="")
my.lm <- lm(xHH$value ~ xHH$extortion + xHH$punishment)
sHH$plane3d(my.lm)