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
outputPath <- "/data/workspace/repast/intERS/output/17-enlarge-40/heterogeneous/LL-LH-HL-HH"

###
### Consolidate the Raw Simulation data
###
numRuns <- 3                               # Number of simulation runs
combinations <- c(0,1,2,3)                 # Combination numbers
dirNames <- c("LL","LH","HL","HH")         # Directories names

for(run in seq(1:numRuns)){
  
  if(run > 1){
    writeColLabels <- FALSE
    appendData <- TRUE
  } else {
    writeColLabels <- TRUE
    appendData <- FALSE
  }

  ### Upload Extorter data
  extorter <- read.big.matrix(paste(outputPath,"/",run,"/extorters.csv",sep=""), sep=";",
                              header=TRUE, type="double", backingfile="extorters.bin",
                              backingpath=paste(outputPath,"/",run,sep=""),
                              descriptorfile="extorters.desc")
  
  ### Upload Observer data
  observer <- read.big.matrix(paste(outputPath,"/",run,"/observers.csv",sep=""), sep=";",
                              header=TRUE, type="double", backingfile="observers.bin",
                              backingpath=paste(outputPath,"/",run,sep=""),
                              descriptorfile="observers.desc")
  
  ### Upload Target data
  target <- read.big.matrix(paste(outputPath,"/",run,"/targets.csv",sep=""), sep=";",
                              header=TRUE, type="double", backingfile="targets.bin",
                              backingpath=paste(outputPath,"/",run,sep=""),
                              descriptorfile="targets.desc")
  
  for(combination in combinations) {
    
    aux <- as.big.matrix(extorter[mwhich(extorter, "type", combination, "eq"), drop=FALSE])
    index <- bigsplit(aux, "cycle",)
    
    extorterAvg <- foreach(i = index, .combine=rbind) %dopar% {
      colmean(as.big.matrix(aux[i, drop=FALSE])) }
    
    extorterSum <- NULL
    extorterSum <- foreach(i = index, .combine=rbind) %dopar% {
      colsum(as.big.matrix(aux[i, drop=FALSE])) }
    
    write.table(extorterAvg,file=paste(outputPath,"/","cAvgExtorters",dirNames[combination + 1],".csv",sep=""),
                sep=";", quote=FALSE, col.names=writeColLabels, row.names=FALSE, append=appendData)
    
    write.table(extorterSum,file=paste(outputPath,"/","cSumExtorters",dirNames[combination + 1],".csv",sep=""),
                sep=";", quote=FALSE, col.names=writeColLabels, row.names=FALSE, append=appendData)
  }
  
  index <- bigsplit(observer, "cycle")
  observerAvg <- foreach(i = index, .combine=rbind) %dopar% {
    colmean(as.big.matrix(observer[i, drop=FALSE])) }
  
  observerSum <- foreach(i = index, .combine=rbind) %dopar% { 
    colsum(as.big.matrix(observer[i, drop=FALSE])) }
  
  index <- bigsplit(target, "cycle")
  targetAvg <- foreach(i = index, .combine=rbind) %dopar% {
    colmean(as.big.matrix(target[i, drop=FALSE])) }
  
  targetSum <- foreach(i = index, .combine=rbind) %dopar% {
    colsum(as.big.matrix(target[i, drop=FALSE])) }
  
  write.table(observerAvg,file=paste(outputPath,"/","cAvgObservers.csv",sep=""), sep=";",
              quote=FALSE, col.names=writeColLabels, row.names=FALSE, append=appendData)
  
  write.table(observerSum,file=paste(outputPath,"/","cSumObservers.csv",sep=""), sep=";",
              quote=FALSE, col.names=writeColLabels, row.names=FALSE, append=appendData)
  
  write.table(targetAvg,file=paste(outputPath,"/","cAvgTargets.csv",sep=""), sep=";",
              quote=FALSE, col.names=writeColLabels, row.names=FALSE, append=appendData)
  
  write.table(targetSum,file=paste(outputPath,"/","cSumTargets.csv",sep=""), sep=";",
              quote=FALSE, col.names=writeColLabels, row.names=FALSE, append=appendData)
}

extorterDist <- NULL
targetDist <- NULL
for(run in seq(1:numRuns)){
  ### Upload Extorter data
  extorter <- read.big.matrix(paste(outputPath,"/",run,"/extorters.csv",sep=""),
                              sep=";", header=TRUE, type="double", backingfile="extorters.bin",
                              backingpath=paste(outputPath,"/",dirNames[combination + 1],"/",run,sep=""),
                              descriptorfile="extorters.desc")
  lastCycle = max(extorter[,"cycle"])
  extorterDist <- rbind(extorterDist, extorter[mwhich(extorter, "cycle", lastCycle, "eq"),
                                               c("type","wealth","numTargets")])
  
  ### Upload Target data
  target <- read.big.matrix(paste(outputPath,"/",run,"/targets.csv",sep=""),
                            sep=";", header=TRUE, type="double", backingfile="targets.bin",
                            backingpath=paste(outputPath,"/",dirNames[combination + 1],"/",run,sep=""),
                            descriptorfile="targets.desc")
  lastCycle = max(target[,"cycle"])
  wealth <- target[mwhich(target, "cycle", lastCycle, "eq"), c("wealth")]
  type <- rep(combination, length(wealth))
  targetDist <- rbind(targetDist, cbind(type, wealth))
}

write.table(extorterDist,file=paste(outputPath,"/cDistExtorters.csv",sep=""),sep=";",
            quote=FALSE,col.names=TRUE,row.names=FALSE)

write.table(targetDist,file=paste(outputPath,"/cDistTargets.csv",sep=""),sep=";",
            quote=FALSE,col.names=TRUE,row.names=FALSE)