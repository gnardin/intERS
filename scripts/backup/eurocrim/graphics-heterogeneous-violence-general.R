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

numViolenceLL <- matrix(0,nrow=4,ncol=5)
numViolenceLH <- matrix(0,nrow=4,ncol=5)
numViolenceHL <- matrix(0,nrow=4,ncol=5)
numViolenceHH <- matrix(0,nrow=4,ncol=5)

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
    
    numViolenceLL[pu-1,ex] <- mean(eLL[1:50,]
                                   [which(numPunishment+numReceivedCounterattack > 0),
                                    (numPunishment+numCounterattack+numReceivedCounterattack)])
    numViolenceLH[pu-1,ex] <- mean(eLH[1:50,]
                                   [which(numPunishment+numReceivedCounterattack > 0),
                                    (numPunishment+numCounterattack+numReceivedCounterattack)])
    numViolenceHL[pu-1,ex] <- mean(eHL[1:50,]
                                   [which(numPunishment+numReceivedCounterattack > 0),
                                    (numPunishment+numCounterattack+numReceivedCounterattack)])
    numViolenceHH[pu-1,ex] <- mean(eHH[1:50,]
                                   [which(numPunishment+numReceivedCounterattack > 0),
                                    (numPunishment+numCounterattack+numReceivedCounterattack)])
  }
}

dimnames(numViolenceLL) <- list(c("[20|40]","[30,60]","[40,80]","[50,100]"),
                                c("[10|20]","[20,40]","[30,60]","[40,80]","[50,100]"))
dimnames(numViolenceLH) <- list(c("[20|40]","[30,60]","[40,80]","[50,100]"),
                                c("[10|20]","[20,40]","[30,60]","[40,80]","[50,100]"))
dimnames(numViolenceHL) <- list(c("[20|40]","[30,60]","[40,80]","[50,100]"),
                                c("[10|20]","[20,40]","[30,60]","[40,80]","[50,100]"))
dimnames(numViolenceHH) <- list(c("[20|40]","[30,60]","[40,80]","[50,100]"),
                                c("[10|20]","[20,40]","[30,60]","[40,80]","[50,100]"))

xLL <- data.frame(extortion=c(col(numViolenceLL)),
                  punishment=c(row(numViolenceLL)), value=c(numViolenceLL))
xLH <- data.frame(extortion=c(col(numViolenceLH)),
                  punishment=c(row(numViolenceLH)), value=c(numViolenceLH))
xHL <- data.frame(extortion=c(col(numViolenceHL)),
                  punishment=c(row(numViolenceHL)), value=c(numViolenceHL))
xHH <- data.frame(extortion=c(col(numViolenceHH)),
                  punishment=c(row(numViolenceHH)), value=c(numViolenceHH))

sLL <- scatterplot3d(xLL, type = "h", lwd = 5, pch=" ", box=FALSE, angle=60,
                     xlab="Extortion", ylab="Punishment", zlab="Number of Violence (LL)",
                     x.ticklabs=colnames(numViolenceLL),
                     y.ticklabs=c("[20,40]","","[30,60]","","[40,80]","","[50,100]"),
                     z.ticklabs="",
                     color = grey(20:1 / 40), main="")
my.lm <- lm(xLL$value ~ xLL$extortion + xLL$punishment)
sLL$plane3d(my.lm)

sLH <- scatterplot3d(xLH, type = "h", lwd = 5, pch=" ", box=FALSE, angle=60,
                     xlab="Extortion", ylab="Punishment", zlab="Number of Violence (LH)",
                     x.ticklabs=colnames(numViolenceLH),
                     y.ticklabs=c("[20,40]","","[30,60]","","[40,80]","","[50,100]"),
                     z.ticklabs="",
                     color = grey(20:1 / 40), main="")
my.lm <- lm(xLH$value ~ xLH$extortion + xLH$punishment)
sLH$plane3d(my.lm)

sHL <- scatterplot3d(xHL, type = "h", lwd = 5, pch=" ", box=FALSE, angle=60,
                     xlab="Extortion", ylab="Punishment", zlab="Number of Violence (HL)",
                     x.ticklabs=colnames(numViolenceHL),
                     y.ticklabs=c("[20,40]","","[30,60]","","[40,80]","","[50,100]"),
                     z.ticklabs="",
                     color = grey(20:1 / 40), main="")
my.lm <- lm(xHL$value ~ xHL$extortion + xHL$punishment)
sHL$plane3d(my.lm)

sHH <- scatterplot3d(xHH, type = "h", lwd = 5, pch=" ", box=FALSE, angle=60,
                     xlab="Extortion", ylab="Punishment", zlab="Number of Violence (HH)",
                     x.ticklabs=colnames(numViolenceHH),
                     y.ticklabs=c("[20,40]","","[30,60]","","[40,80]","","[50,100]"),
                     z.ticklabs="",
                     color = grey(20:1 / 40), main="")
my.lm <- lm(xHH$value ~ xHH$extortion + xHH$punishment)
sHH$plane3d(my.lm)