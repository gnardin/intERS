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

numProtectionLL <- matrix(0,nrow=4,ncol=5)
numProtectionLH <- matrix(0,nrow=4,ncol=5)
numProtectionHL <- matrix(0,nrow=4,ncol=5)
numProtectionHH <- matrix(0,nrow=4,ncol=5)

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
    
    numProtectionLL[pu-1,ex] <- mean(eLL[1:50,][which(numHelpRequested > 0),
                                                (numHelpRequested-numReceivedCounterattack)])
    numProtectionLH[pu-1,ex] <- mean(eLH[1:50,][which(numHelpRequested > 0),
                                                (numHelpRequested-numReceivedCounterattack)])
    numProtectionHL[pu-1,ex] <- mean(eHL[1:50,][which(numHelpRequested > 0),
                                                (numHelpRequested-numReceivedCounterattack)])
    numProtectionHH[pu-1,ex] <- mean(eHH[1:50,][which(numHelpRequested > 0),
                                                (numHelpRequested-numReceivedCounterattack)])
  }
}

dimnames(numProtectionLL) <- list(c("[20|40]","[30,60]","[40,80]","[50,100]"),
                                  c("[10|20]","[20,40]","[30,60]","[40,80]","[50,100]"))
dimnames(numProtectionLH) <- list(c("[20|40]","[30,60]","[40,80]","[50,100]"),
                                  c("[10|20]","[20,40]","[30,60]","[40,80]","[50,100]"))
dimnames(numProtectionHL) <- list(c("[20|40]","[30,60]","[40,80]","[50,100]"),
                                  c("[10|20]","[20,40]","[30,60]","[40,80]","[50,100]"))
dimnames(numProtectionHH) <- list(c("[20|40]","[30,60]","[40,80]","[50,100]"),
                                  c("[10|20]","[20,40]","[30,60]","[40,80]","[50,100]"))

xLL <- data.frame(extortion=c(col(numProtectionLL)),
                  punishment=c(row(numProtectionLL)), value=c(numProtectionLL))
xLH <- data.frame(extortion=c(col(numProtectionLH)),
                  punishment=c(row(numProtectionLH)), value=c(numProtectionLH))
xHL <- data.frame(extortion=c(col(numProtectionHL)),
                  punishment=c(row(numProtectionHL)), value=c(numProtectionHL))
xHH <- data.frame(extortion=c(col(numProtectionHH)),
                  punishment=c(row(numProtectionHH)), value=c(numProtectionHH))

sLL <- scatterplot3d(xLL, type = "h", lwd = 5, pch=" ", box=FALSE, angle=60,
                     xlab="Extortion", ylab="Punishment", zlab="Number of Protection (LL)",
                     x.ticklabs=colnames(numProtectionLL),
                     y.ticklabs=c("[20,40]","","[30,60]","","[40,80]","","[50,100]"),
                     z.ticklabs="",
                     color = grey(20:1 / 40), main="")
my.lm <- lm(xLL$value ~ xLL$extortion + xLL$punishment)
sLL$plane3d(my.lm)

sLH <- scatterplot3d(xLH, type = "h", lwd = 5, pch=" ", box=FALSE, angle=60,
                     xlab="Extortion", ylab="Punishment", zlab="Number of Protection (LH)",
                     x.ticklabs=colnames(numProtectionLH),
                     y.ticklabs=c("[20,40]","","[30,60]","","[40,80]","","[50,100]"),
                     z.ticklabs="",
                     color = grey(20:1 / 40), main="")
my.lm <- lm(xLH$value ~ xLH$extortion + xLH$punishment)
sLH$plane3d(my.lm)

sHL <- scatterplot3d(xHL, type = "h", lwd = 5, pch=" ", box=FALSE, angle=60,
                     xlab="Extortion", ylab="Punishment", zlab="Number of Protection (HL)",
                     x.ticklabs=colnames(numProtectionHL),
                     y.ticklabs=c("[20,40]","","[30,60]","","[40,80]","","[50,100]"),
                     z.ticklabs="",
                     color = grey(20:1 / 40), main="")
my.lm <- lm(xHL$value ~ xHL$extortion + xHL$punishment)
sHL$plane3d(my.lm)

sHH <- scatterplot3d(xHH, type = "h", lwd = 5, pch=" ", box=FALSE, angle=60,
                     xlab="Extortion", ylab="Punishment", zlab="Number of Protection (HH)",
                     x.ticklabs=colnames(numProtectionHH),
                     y.ticklabs=c("[20,40]","","[30,60]","","[40,80]","","[50,100]"),
                     z.ticklabs="",
                     color = grey(20:1 / 40), main="")
my.lm <- lm(xHH$value ~ xHH$extortion + xHH$punishment)
sHH$plane3d(my.lm)