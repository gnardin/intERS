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

numPunishmentLL <- matrix(0,nrow=4,ncol=5)
numPunishmentLH <- matrix(0,nrow=4,ncol=5)
numPunishmentHL <- matrix(0,nrow=4,ncol=5)
numPunishmentHH <- matrix(0,nrow=4,ncol=5)

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
    
    numPunishmentLL[pu-1,ex] <- mean(eLL[1:50,][which(numExtortion > 0),(numPunishment)])
    numPunishmentLH[pu-1,ex] <- mean(eLH[1:50,][which(numExtortion > 0),(numPunishment)])
    numPunishmentHL[pu-1,ex] <- mean(eHL[1:50,][which(numExtortion > 0),(numPunishment)])
    numPunishmentHH[pu-1,ex] <- mean(eHH[1:50,][which(numExtortion > 0),(numPunishment)])
  }
}

dimnames(numPunishmentLL) <- list(c("[20|40]","[30,60]","[40,80]","[50,100]"),
                                c("[10|20]","[20,40]","[30,60]","[40,80]","[50,100]"))
dimnames(numPunishmentLH) <- list(c("[20|40]","[30,60]","[40,80]","[50,100]"),
                                c("[10|20]","[20,40]","[30,60]","[40,80]","[50,100]"))
dimnames(numPunishmentHL) <- list(c("[20|40]","[30,60]","[40,80]","[50,100]"),
                                c("[10|20]","[20,40]","[30,60]","[40,80]","[50,100]"))
dimnames(numPunishmentHH) <- list(c("[20|40]","[30,60]","[40,80]","[50,100]"),
                                c("[10|20]","[20,40]","[30,60]","[40,80]","[50,100]"))

xLL <- data.frame(extortion=c(col(numPunishmentLL)),
                  punishment=c(row(numPunishmentLL)), value=c(numPunishmentLL))
xLH <- data.frame(extortion=c(col(numPunishmentLH)),
                  punishment=c(row(numPunishmentLH)), value=c(numPunishmentLH))
xHL <- data.frame(extortion=c(col(numPunishmentHL)),
                  punishment=c(row(numPunishmentHL)), value=c(numPunishmentHL))
xHH <- data.frame(extortion=c(col(numPunishmentHH)),
                  punishment=c(row(numPunishmentHH)), value=c(numPunishmentHH))

sLL <- scatterplot3d(xLL, type = "h", lwd = 5, pch=" ", box=FALSE, angle=60,
                     xlab="Extortion", ylab="Punishment", zlab="Number of Punishment (LL)",
                     x.ticklabs=colnames(numPunishmentLL),
                     y.ticklabs=c("[20,40]","","[30,60]","","[40,80]","","[50,100]"),
                     z.ticklabs="",
                     color = grey(20:1 / 40), main="")
my.lm <- lm(xLL$value ~ xLL$extortion + xLL$punishment)
sLL$plane3d(my.lm)

sLH <- scatterplot3d(xLH, type = "h", lwd = 5, pch=" ", box=FALSE, angle=60,
                     xlab="Extortion", ylab="Punishment", zlab="Number of Punishment (LH)",
                     x.ticklabs=colnames(numPunishmentLH),
                     y.ticklabs=c("[20,40]","","[30,60]","","[40,80]","","[50,100]"),
                     z.ticklabs="",
                     color = grey(20:1 / 40), main="")
my.lm <- lm(xLH$value ~ xLH$extortion + xLH$punishment)
sLH$plane3d(my.lm)

sHL <- scatterplot3d(xHL, type = "h", lwd = 5, pch=" ", box=FALSE, angle=60,
                     xlab="Extortion", ylab="Punishment", zlab="Number of Punishment (HL)",
                     x.ticklabs=colnames(numPunishmentHL),
                     y.ticklabs=c("[20,40]","","[30,60]","","[40,80]","","[50,100]"),
                     z.ticklabs="",
                     color = grey(20:1 / 40), main="")
my.lm <- lm(xHL$value ~ xHL$extortion + xHL$punishment)
sHL$plane3d(my.lm)

sHH <- scatterplot3d(xHH, type = "h", lwd = 5, pch=" ", box=FALSE, angle=60,
                     xlab="Extortion", ylab="Punishment", zlab="Number of Punishment (HH)",
                     x.ticklabs=colnames(numPunishmentHH),
                     y.ticklabs=c("[20,40]","","[30,60]","","[40,80]","","[50,100]"),
                     z.ticklabs="",
                     color = grey(20:1 / 40), main="")
my.lm <- lm(xHH$value ~ xHH$extortion + xHH$punishment)
sHH$plane3d(my.lm)