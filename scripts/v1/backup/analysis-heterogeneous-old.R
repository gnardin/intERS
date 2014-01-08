###
### Required libraries
###
library(data.table)

###
### Raw simulation data output directory
###
outputPath <- "/data/workspace/repast/intERS/output/28-enlarge-20-counterattack-20/heterogeneous/LL-LH-HL-HH"

###
### Consolidate the Raw Simulation data
###
numRuns <- 30                              # Number of simulation runs
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
  extorter <- read.csv(paste(outputPath,"/",run,"/extorters.csv",sep=""), sep=";", header=TRUE)
  
  ### Upload Observer data
  observer <- read.csv(paste(outputPath,"/",run,"/observers.csv",sep=""), sep=";", header=TRUE)
  
  ### Upload Target data
  target <- read.csv(paste(outputPath,"/",run,"/targets.csv",sep=""), sep=";", header=TRUE)
  
  for(combination in combinations){
    
    extorterF <- extorter[extorter$type == combination,]
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
    
  observerF <- data.table(observer)
  setkey(observerF, type, cycle)
  observerF <- observerF[,which(!grepl("type", colnames(observerF))), with = FALSE]
  observerAvg <- observerF[, lapply(.SD,mean), by=cycle]
  observerSum <- observerF[, lapply(.SD,sum), by=cycle]
  
  targetF <- data.table(target)
  setkey(targetF, type, cycle)
  targetF <- targetF[,which(!grepl("type", colnames(targetF))), with = FALSE]
  targetAvg <- targetF[, lapply(.SD,mean), by=cycle]
  targetSum <- targetF[, lapply(.SD,sum), by=cycle]
  
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
  extorter <- read.csv(paste(outputPath,"/",run,"/extorters.csv",sep=""),
                       sep=";", header=TRUE)
  extorter <- data.table(extorter)
  setkey(extorter, cycle)
  lastCycle = max(as.data.frame(extorter)[,"cycle"])
  extorterDist <- rbind(extorterDist, extorter[which(cycle==lastCycle),list(type,wealth,numTargets)])
  
  target <- read.csv(paste(outputPath,"/",run,"/targets.csv",sep=""),
                     sep=";", header=TRUE)
  target <- data.table(target)
  setkey(target, cycle)
  lastCycle = max(as.data.frame(target)[,"cycle"])
  targetDist <- rbind(targetDist, target[which(cycle==lastCycle),list(type,wealth)])
}

write.table(extorterDist,file=paste(outputPath,"/cDistExtorters.csv",sep=""),sep=";",
            quote=FALSE,col.names=TRUE,row.names=FALSE)

write.table(targetDist,file=paste(outputPath,"/cDistTargets.csv",sep=""),sep=";",
            quote=FALSE,col.names=TRUE,row.names=FALSE)