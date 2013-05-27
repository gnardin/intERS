###
### Required libraries
###
library(data.table)

###
### Command Arguments
###
args <- commandArgs(TRUE)

###
### Raw simulation data output directory
###
outputPath <- args[1]

###
### Consolidate the Raw Simulation data
###
numRuns <- 30                              # Number of simulation runs
combinations <- c(0,1,2,3)                 # Combination numbers
dirNames <- c("LL","LH","HL","HH")         # Directories names

extorterDist <- NULL
targetDist <- NULL
for(run in seq(1:numRuns)){

  if(run > 1){
    writeColLabels <- FALSE
    appendData <- TRUE
  } else {
    writeColLabels <- TRUE
    appendData <- FALSE
  }
  
  ### Upload Extorter data
  extorter <- read.csv(paste(outputPath,"/",run,"/extorters.csv",sep=""), sep=";", header=TRUE)
  
  extorter <- data.table(extorter)
  setkey(extorter, type, cycle)
  lastCycle = max(as.data.frame(extorter)[,"cycle"])
  extorterDist <- rbind(extorterDist, extorter[cycle==lastCycle, list(type, wealth, numTargets)])

  for(combination in combinations){
    
    extorterF <- extorter[which(type==combination),]
    extorterF <- data.table(extorterF)
    setkey(extorterF, type, cycle)
    extorterF <- extorterF[,which(!grepl("type", colnames(extorterF))), with = FALSE]
    extorterAvg <- extorterF[, lapply(.SD,mean), by=cycle]
    extorterSum <- extorterF[, lapply(.SD,sum), by=cycle]
    
    write.table(extorterAvg,file=paste(outputPath,"/","cAvgExtorters",dirNames[combination + 1],".csv",sep=""),
                sep=";",quote=FALSE, col.names=writeColLabels, row.names=FALSE, append=appendData)
    
    write.table(extorterSum,file=paste(outputPath,"/","cSumExtorters",dirNames[combination + 1],".csv",sep=""),
                sep=";",quote=FALSE, col.names=writeColLabels, row.names=FALSE, append=appendData)
  }
  
  
  ### Upload Observer data
  observer <- read.csv(paste(outputPath,"/",run,"/observers.csv",sep=""), sep=";", header=TRUE)
  
  observer <- data.table(observer)
  setkey(observer, type, cycle)
  observer <- observer[,which(!grepl("type", colnames(observer))), with = FALSE]
  observerAvg <- observer[, lapply(.SD,mean), by=cycle]
  observerSum <- observer[, lapply(.SD,sum), by=cycle]
  
  write.table(observerAvg,file=paste(outputPath,"/","cAvgObservers.csv",sep=""), sep=";",
              quote=FALSE, col.names=writeColLabels, row.names=FALSE, append=appendData)
  
  write.table(observerSum,file=paste(outputPath,"/","cSumObservers.csv",sep=""), sep=";",
              quote=FALSE, col.names=writeColLabels, row.names=FALSE, append=appendData)
  
  
  ### Upload Target data
  target <- read.csv(paste(outputPath,"/",run,"/targets.csv",sep=""), sep=";", header=TRUE)
  
  target <- data.table(target)
  setkey(target, type, cycle)
  
  lastCycle = max(as.data.frame(target)[,"cycle"])
  targetDist <- rbind(targetDist, target[cycle==lastCycle, list(type, wealth)])
  
  target <- target[,which(!grepl("type", colnames(target))), with = FALSE]
  targetAvg <- target[, lapply(.SD,mean), by=cycle]
  targetSum <- target[, lapply(.SD,sum), by=cycle]
  
  write.table(targetAvg,file=paste(outputPath,"/","cAvgTargets.csv",sep=""), sep=";",
              quote=FALSE, col.names=writeColLabels, row.names=FALSE, append=appendData)
  
  write.table(targetSum,file=paste(outputPath,"/","cSumTargets.csv",sep=""), sep=";",
              quote=FALSE, col.names=writeColLabels, row.names=FALSE, append=appendData)
}

write.table(extorterDist,file=paste(outputPath,"/cDistExtorters.csv",sep=""),sep=";",
            quote=FALSE,col.names=TRUE,row.names=FALSE)

write.table(targetDist,file=paste(outputPath,"/cDistTargets.csv",sep=""),sep=";",
            quote=FALSE,col.names=TRUE,row.names=FALSE)
