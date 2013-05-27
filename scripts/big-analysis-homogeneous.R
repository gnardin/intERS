###
### Required libraries
###
library(data.table)
library(ggplot2)
library(gridExtra)
library(bigmemory)
library(biganalytics)
library(bigtabulate)
library(doMC)
registerDoMC(3)

###
### Raw simulation data output directory
###
outputPath <- "/data/workspace/repast/intERS/output/17-enlarge-40/homogeneous"

###
### Consolidate the Raw Simulation data
###
numRuns <- 3                               # Number of simulation runs
combinations <- c(0,1,2,3)                 # Combination numbers
dirNames <- c("LL","LH","HL","HH")         # Directories names

for(combination in combinations){
  
  for(run in seq(1:numRuns)){
    
    if(run > 1){
      writeColLabels <- FALSE
      appendData <- TRUE
    } else {
      writeColLabels <- TRUE
      appendData <- FALSE
    }
    
    ### Upload Extorter data
    extorter <- read.big.matrix(paste(outputPath,"/",dirNames[combination + 1],"/",run,"/extorters.csv",sep=""),
                                sep=";", header=TRUE, type="double", backingfile="extorters.bin",
                                backingpath=paste(outputPath,"/",dirNames[combination + 1],"/",run,sep=""),
                                descriptorfile="extorters.desc")
    
    index <- bigsplit(extorter, "cycle")
    extorterAvg <- foreach(i = index, .combine=rbind) %dopar% {
      colmean(as.big.matrix(extorter[i, drop=FALSE])) }
    
    extorterSum <- foreach(i = index, .combine=rbind) %dopar% {
      colsum(as.big.matrix(extorter[i, drop=FALSE])) }
    
    ### Upload Observer data
    observer <- read.big.matrix(paste(outputPath,"/",dirNames[combination + 1],"/",run,"/observers.csv",sep=""),
                                sep=";", header=TRUE, type="double", backingfile="observers.bin",
                                backingpath=paste(outputPath,"/",dirNames[combination + 1],"/",run,sep=""),
                                descriptorfile="observers.desc")
    
    index <- bigsplit(observer, "cycle")
    observerAvg <- foreach(i = index, .combine=rbind) %dopar% {
      colmean(as.big.matrix(observer[i, drop=FALSE])) }
    
    observerSum <- foreach(i = index, .combine=rbind) %dopar% {
      colsum(as.big.matrix(observer[i, drop=FALSE])) }
    
    ### Upload Target data
    target <- read.big.matrix(paste(outputPath,"/",dirNames[combination + 1],"/",run,"/targets.csv",sep=""),
                                sep=";", header=TRUE, type="double", backingfile="targets.bin",
                                backingpath=paste(outputPath,"/",dirNames[combination + 1],"/",run,sep=""),
                                descriptorfile="targets.desc")
    
    index <- bigsplit(target, "cycle")
    targetAvg <- foreach(i = index, .combine=rbind) %dopar% {
      colmean(as.big.matrix(target[i, drop=FALSE])) }
    
    targetSum <- foreach(i = index, .combine=rbind) %dopar% {
      colsum(as.big.matrix(target[i, drop=FALSE])) }
    
  
    write.table(extorterAvg,file=paste(outputPath,"/","cAvgExtorters",dirNames[combination + 1],".csv",sep=""),
                sep=";", quote=FALSE, col.names=writeColLabels, row.names=FALSE, append=appendData)
  
    write.table(extorterSum,file=paste(outputPath,"/","cSumExtorters",dirNames[combination + 1],".csv",sep=""),
                sep=";", quote=FALSE, col.names=writeColLabels, row.names=FALSE, append=appendData)

    write.table(observerAvg,file=paste(outputPath,"/","cAvgObservers",dirNames[combination + 1],".csv",sep=""),
                sep=";", quote=FALSE, col.names=writeColLabels, row.names=FALSE, append=appendData)
  
    write.table(observerSum,file=paste(outputPath,"/","cSumObservers",dirNames[combination + 1],".csv",sep=""),
                sep=";", quote=FALSE, col.names=writeColLabels, row.names=FALSE, append=appendData)
  
    write.table(targetAvg,file=paste(outputPath,"/","cAvgTargets",dirNames[combination + 1],".csv",sep=""),
                sep=";", quote=FALSE, col.names=writeColLabels, row.names=FALSE, append=appendData)
  
    write.table(targetSum,file=paste(outputPath,"/","cSumTargets",dirNames[combination + 1],".csv",sep=""),
                sep=";", quote=FALSE, col.names=writeColLabels, row.names=FALSE, append=appendData)
  }
}

extorterDist <- NULL
targetDist <- NULL
for(combination in combinations){
  for(run in seq(1:numRuns)){
    
    ### Upload Extorter data
    extorter <- read.big.matrix(paste(outputPath,"/",dirNames[combination + 1],"/",run,"/extorters.csv",sep=""),
                                sep=";", header=TRUE, type="double", backingfile="extorters.bin",
                                backingpath=paste(outputPath,"/",dirNames[combination + 1],"/",run,sep=""),
                                descriptorfile="extorters.desc")
    lastCycle = max(extorter[,"cycle"])
    extorterDist <- rbind(extorterDist, extorter[mwhich(extorter, "cycle", lastCycle, "eq"),
                                                 c("type","wealth","numTargets")])
    
    ### Upload Target data
    target <- read.big.matrix(paste(outputPath,"/",dirNames[combination + 1],"/",run,"/targets.csv",sep=""),
                              sep=";", header=TRUE, type="double", backingfile="targets.bin",
                              backingpath=paste(outputPath,"/",dirNames[combination + 1],"/",run,sep=""),
                              descriptorfile="targets.desc")
    lastCycle = max(target[,"cycle"])
    wealth <- target[mwhich(target, "cycle", lastCycle, "eq"), c("wealth")]
    type <- rep(combination, length(wealth))
    targetDist <- rbind(targetDist, cbind(type, wealth))
  }
}

write.table(extorterDist,file=paste(outputPath,"/cDistExtorters.csv",sep=""), sep=";",
            quote=FALSE, col.names=TRUE, row.names=FALSE)

write.table(targetDist,file=paste(outputPath,"/cDistTargets.csv",sep=""), sep=";",
            quote=FALSE, col.names=TRUE, row.names=FALSE)