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

numTargets <- matrix(0,nrow=4,ncol=5)

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

numTargets[pu-1,ex] <- observerSum[cycle == NROW(observerSum),TA0]


  }
}

dimnames(numTargets) <- list(c("[20,40]","[30,60]","[40,80]","[50,100]"),
                             c("[10,20]","[20,40]","[30,60]","[40,80]","[50,100]"))

x <- data.frame(extortion=c(col(numTargets)),
                punishment=c(row(numTargets)), value=c(numTargets))

s3d <- scatterplot3d(x, type = "h", lwd = 5, pch=" ", box=FALSE, angle=60,
              xlab="Extortion", ylab="Punishment", zlab="Number of Live Targets",
              x.ticklabs=colnames(numTargets),
              y.ticklabs=c("[20,40]","","[30,60]","","[40,80]","","[50,100]"),
              z.ticklabs="",
              color = grey(20:1 / 40), main="")

my.lm <- lm(x$value ~ x$extortion + x$punishment)
s3d$plane3d(my.lm)