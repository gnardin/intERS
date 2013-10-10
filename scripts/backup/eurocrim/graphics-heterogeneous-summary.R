###
### Required libraries
###
library(data.table)
library(ggplot2)
library(gridExtra)

###
### Command Arguments
###
args <- commandArgs(TRUE)

###
### Raw simulation data output directory
###
en <- as.numeric(args[1])
to <- as.numeric(args[2])
ex <- as.numeric(args[3])
pu <- as.numeric(args[4])

###
### Comment all those lines when using with Rscript
###
#rm(list=ls())
#en <- 10
#to <- 10
#ex <- 10
#pu <- 20

###
### Raw simulation data output directory
###
basePath <- "/data/workspace/gloders/intERS/output"

outputPath <- paste(basePath,"/En",en,"_To",to,
                    "/Ex",ex,"-",(ex*2),"_Pu",pu,"-",(pu*2),"_En",en,"_To",to,
                    "/LL-LH-HL-HH", sep="")
        
###
### Images repository directory
###
dir.create(file.path(basePath, "image_summary"), showWarnings = FALSE)
imagePath <- paste(basePath,"/image_summary", sep="")

ext <- paste("-Ex",ex,"-",(ex*2),"_Pu",pu,"-",(pu*2),"_En",en,"_To",to, sep="")

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


###
### NUMBER OF TARGETS
###
minTotalTargetsY <- floor(min(observerSum[,totalTargets]))
maxTotalTargetsY <- ceiling(max(observerSum[,totalTargets]))

png(file=paste(imagePath,"/numberTargets",ext,".png", sep=""),
    width=800, height=600)
qplot(cycle, totalTargets, data=observerSum, geom = "line",
      ylim = c(minTotalTargetsY, maxTotalTargetsY),
      main="Number of Targets", xlab="", ylab="Number")
dev.off()


###
### NUMBER OF TARGETS MINIMUM EQUAL 0
###
minTotalTargetsY <- 0
maxTotalTargetsY <- ceiling(max(observerSum[,totalTargets]))

png(file=paste(imagePath,"/numberTargetsMin0",ext,".png", sep=""),
    width=800, height=600)
qplot(cycle, totalTargets, data=observerSum, geom = "line",
      ylim = c(minTotalTargetsY, maxTotalTargetsY),
      main="Number of Targets", xlab="", ylab="Number")
dev.off()


###
### TARGETS ACCUMULATED WEALTH
####
minWealthY <- floor(min(targetSum[,wealth]))
maxWealthY <- ceiling(max(targetSum[,wealth]))

png(file=paste(imagePath,"/accumulatedTargetsWealth",ext,".png", sep=""),
    width=800, height=600)
qplot(cycle, wealth, data=targetSum, geom = "line",
      ylim = c(minWealthY,maxWealthY),
      main="Targets Accumulated Wealth", xlab="", ylab="Wealth")
dev.off()



###
### NUMBER OF TOTAL EXTORTERS
###
minTotalExtortersY <- 0
maxTotalExtortersY <- ceiling(max(observerAvg[,totalExtortersFree+totalExtortersImprisoned]))

oX <- as.numeric(min((1:NROW(observerAvg))[(observerAvg$totalExtortersFree +
                                              observerAvg$totalExtortersImprisoned) < 1],
                     NROW(observerAvg)+1))

o <- observerAvg
print(oX)
if(oX <= NROW(observerAvg)){
  o <- observerAvg[oX:NROW(observerAvg), totalExtortersFree := 0]
  o <- observerAvg[oX:NROW(observerAvg), totalExtortersImprisoned := 0]
}

png(file=paste(imagePath,"/numberExtortersTotal",ext,".png", sep=""), width=800, height=600)
qplot(cycle, totalExtortersFree+totalExtortersImprisoned, data=o, geom = "line",
      ylim = c(minTotalExtortersY,maxTotalExtortersY),
      main="Total Number of Extorters", xlab="", ylab="Number")
dev.off()



###
### NUMBER OF EXTORTERS PER TARGET BEING EXTORTED
###
minExtortersPerTargetY <- 0
maxExtortersPerTargetY <- ceiling(max(observerSum[,NE0/TE0], na.rm = TRUE))

png(file=paste(imagePath,"/numberExtortersPerTarget",ext,".png", sep=""),
    width=800, height=600)
qplot(cycle, NE0/TE0, data=observerSum, geom = "line",
      ylim = c(minExtortersPerTargetY,maxExtortersPerTargetY),
      main="Number of Extorters Per Extorted Target", xlab="", ylab="Number")
dev.off()


###
### INDEX NUMBER OF EXTORTERS < 1
###
iLL <- as.numeric(min((1:NROW(observerAvg))[(observerAvg$FR0 +
                                               observerAvg$IM0) < 1],
                      NROW(observerAvg)+1))
iLH <- as.numeric(min((1:NROW(observerAvg))[(observerAvg$FR1 +
                                               observerAvg$IM1) < 1],
                      NROW(observerAvg)+1))
iHL <- as.numeric(min((1:NROW(observerAvg))[(observerAvg$FR2 +
                                               observerAvg$IM2) < 1],
                      NROW(observerAvg)+1))
iHH <- as.numeric(min((1:NROW(observerAvg))[(observerAvg$FR3 +
                                               observerAvg$IM3) < 1],
                      NROW(observerAvg)+1))


###
### NUMBER OF EXTORTERS BY TYPE
###
minExtortersY <- 0
maxExtortersY <- ceiling(max(max(observerAvg[,FR0+IM0],observerAvg[,FR3+IM3]),
                             max(observerAvg[,FR1+IM1],observerAvg[,FR2+IM2])))

oLL <- observerAvg[FR0+IM0 < 1, FR0 := 0]
oLH <- observerAvg[FR1+IM1 < 1, FR1 := 0]
oHL <- observerAvg[FR2+IM2 < 1, FR2 := 0]
oHH <- observerAvg[FR3+IM3 < 1, FR3 := 0]

plotLL <- qplot(cycle, FR0+IM0, data=observerAvg, geom = "line",
                ylim = c(minExtortersY,maxExtortersY),
                main="LL", xlab="", ylab="Number") +
  geom_vline(xintercept=iLL, linetype="dashed", color="red")
plotLH <- qplot(cycle, FR1+IM1, data=observerAvg, geom = "line",
                ylim = c(minExtortersY,maxExtortersY),
                main="LH", xlab="", ylab="Number") +
  geom_vline(xintercept=iLH, linetype="dashed", color="red")
plotHL <- qplot(cycle, FR2+IM2, data=observerAvg, geom = "line",
                ylim = c(minExtortersY,maxExtortersY),
                main="HL", xlab="", ylab="Number") +
  geom_vline(xintercept=iHL, linetype="dashed", color="red")
plotHH <- qplot(cycle, FR3+IM3, data=observerAvg, geom = "line",
                ylim = c(minExtortersY,maxExtortersY),
                main="HH", xlab="", ylab="Number") +
  geom_vline(xintercept=iHH, linetype="dashed", color="red")
png(file=paste(imagePath,"/numberExtortersType",ext,".png", sep=""),
    width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Number of Extorters")
dev.off()


###
### EXTORTER ACCUMULATED WEALTH
###
eLL <- extorterSum[which(type == 0),]
eLH <- extorterSum[which(type == 1),]
eHL <- extorterSum[which(type == 2),]
eHH <- extorterSum[which(type == 3),]

if(iLL <= NROW(eLL)){
  eLL <- eLL[iLL:NROW(eLL), wealth := 0]
}
if(iLH <= NROW(eLH)){
  eLH <- eLH[iLH:NROW(eLH), wealth := 0]
}
if(iHL <= NROW(eHL)){
  eHL <- eHL[iHL:NROW(eHL), wealth := 0]
}
if(iHH <= NROW(eHH)){
  eHH <- eHH[iHH:NROW(eHH), wealth := 0]
}

minWealthY <- floor(min(min(eLL[,wealth],eHH[,wealth]),
                        min(eLH[,wealth],eHL[,wealth])))
maxWealthY <- ceiling(max(max(eLL[,wealth],eHH[,wealth]),
                          max(eLH[,wealth],eHL[,wealth])))

plotLL <- qplot(cycle, wealth, data=eLL, geom = "line",
                ylim = c(minWealthY,maxWealthY),
                main="LL", xlab="", ylab="Wealth") +
  geom_vline(xintercept=iLL, linetype="dashed", color="red")
plotLH <- qplot(cycle, wealth, data=eLH, geom = "line",
                ylim = c(minWealthY,maxWealthY),
                main="LH", xlab="", ylab="Wealth") +
  geom_vline(xintercept=iLH, linetype="dashed", color="red")
plotHL <- qplot(cycle, wealth, data=eHL, geom = "line",
                ylim = c(minWealthY,maxWealthY),
                main="HL", xlab="", ylab="Wealth") +
  geom_vline(xintercept=iHL, linetype="dashed", color="red")
plotHH <- qplot(cycle, wealth, data=eHH, geom = "line",
                ylim = c(minWealthY,maxWealthY),
                main="HH", xlab="", ylab="Wealth") +
  geom_vline(xintercept=iHH, linetype="dashed", color="red")
png(file=paste(imagePath,"/accumulatedExtortersWealth",ext,".png", sep=""),
    width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Extorters Accumulated Wealth")
dev.off()


###
### AVERAGE NUMBER OF TARGETS PER EXTORTER
###
eLL <- extorterAvg[which(type == 0), ]
eLH <- extorterAvg[which(type == 1), ]
eHL <- extorterAvg[which(type == 2), ]
eHH <- extorterAvg[which(type == 3), ]

if(iLL <= NROW(eLL)){
  eLL <- eLL[iLL:NROW(eLL), numTargets := 0]
}
if(iLH <= NROW(eLH)){
  eLH <- eLH[iLH:NROW(eLH), numTargets := 0]
}
if(iHL <= NROW(eHL)){
  eHL <- eHL[iHL:NROW(eHL), numTargets := 0]
}
if(iHH <= NROW(eHH)){
  eHH <- eHH[iHH:NROW(eHH), numTargets := 0]
}

minNumTargetsY <- 0
maxNumTargetsY <- ceiling(max(max(eLL[,numTargets],eHH[,numTargets]),
                              max(eLH[,numTargets],eHL[,numTargets])))

plotLL <- qplot(cycle, numTargets, data=eLL, geom = "line",
                ylim = c(minNumTargetsY,maxNumTargetsY),
                main="LL", xlab="", ylab="Number") +
  geom_vline(xintercept=iLL, linetype="dashed", color="red")
plotLH <- qplot(cycle, numTargets, data=eLH, geom = "line",
                ylim = c(minNumTargetsY,maxNumTargetsY),
                main="LH", xlab="", ylab="Number") +
  geom_vline(xintercept=iLH, linetype="dashed", color="red")
plotHL <- qplot(cycle, numTargets, data=eHL, geom = "line",
                ylim = c(minNumTargetsY,maxNumTargetsY),
                main="HL", xlab="", ylab="Number") +
  geom_vline(xintercept=iHL, linetype="dashed", color="red")
plotHH <- qplot(cycle, numTargets, data=eHH, geom = "line",
                ylim = c(minNumTargetsY,maxNumTargetsY),
                main="HH", xlab="", ylab="Number") +
  geom_vline(xintercept=iHH, linetype="dashed", color="red")
png(file=paste(imagePath,"/numberTargetsPerExtorter",ext,".png", sep=""),
    width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Number of Targets per Extorter")
dev.off()


###
### EXTORTER LOSS OF WEALTH ON FIGHTING
###
eLL <- extorterSum[which(type == 0), ]
eLH <- extorterSum[which(type == 1), ]
eHL <- extorterSum[which(type == 2), ]
eHH <- extorterSum[which(type == 3), ]

if(iLL <= NROW(eLL)){
  eLL <- eLL[iLL:NROW(eLL), totalLostFight := 0]
  eLL <- eLL[iLL:NROW(eLL), totalLostPunishment := 0]
}
if(iLH <= NROW(eLH)){
  eLH <- eLH[iLH:NROW(eLH), totalLostFight := 0]
  eLH <- eLH[iLH:NROW(eLH), totalLostPunishment := 0]
}
if(iHL <= NROW(eHL)){
  eHL <- eHL[iHL:NROW(eHL), totalLostFight := 0]
  eHL <- eHL[iHL:NROW(eHL), totalLostPunishment := 0]
}
if(iHH <= NROW(eHH)){
  eHH <- eHH[iHH:NROW(eHH), totalLostFight := 0]
  eHH <- eHH[iHH:NROW(eHH), totalLostPunishment := 0]
}

lastLine <- max(max(nrow(eLL),nrow(eHH)),max(nrow(eLH),nrow(eHL)))

for(index in 2:lastLine){
  
  if(index < nrow(eLL)){
    aux <- eLL[index-1,totalLostFight] + eLL[index,totalLostFight]
    eLL[index,totalLostFight := aux]
    aux <- eLL[index-1,totalLostPunishment] + eLL[index,totalLostPunishment]
    eLL[index,totalLostPunishment := aux]
  }
  
  if(index < nrow(eLH)){
    aux <- eLH[index-1,totalLostFight] + eLH[index,totalLostFight]
    eLH[index,totalLostFight := aux]
    aux <- eLH[index-1,totalLostPunishment] + eLH[index,totalLostPunishment]
    eLH[index,totalLostPunishment := aux]
  }
  
  if(index < nrow(eHL)){
    aux <- eHL[index-1,totalLostFight] + eHL[index,totalLostFight]
    eHL[index,totalLostFight := aux]
    aux <- eHL[index-1,totalLostPunishment] + eHL[index,totalLostPunishment]
    eHL[index,totalLostPunishment := aux]
  }
  
  if(index < nrow(eHH)){
    aux <- eHH[index-1,totalLostFight] + eHH[index,totalLostFight]
    eHH[index,totalLostFight := aux]
    aux <- eHH[index-1,totalLostPunishment] + eHH[index,totalLostPunishment]
    eHH[index,totalLostPunishment := aux]
  }
}

minFightY <- floor(min(min(eLL[,totalLostFight], eHH[,totalLostFight]),
                       min(eLH[,totalLostFight], eHL[,totalLostFight])))
maxFightY <- ceiling(max(max(eLL[,totalLostFight], eHH[,totalLostFight]),
                         max(eLH[,totalLostFight], eHL[,totalLostFight])))

plotLL <- qplot(cycle, totalLostFight, data=eLL, geom = "line",
                ylim = c(minFightY,maxFightY),
                main="LL", xlab="", ylab="Wealth") +
  geom_vline(xintercept=iLL, linetype="dashed", color="red")
plotLH <- qplot(cycle, totalLostFight, data=eLH, geom = "line",
                ylim = c(minFightY,maxFightY),
                main="LH", xlab="", ylab="Wealth") +
  geom_vline(xintercept=iLH, linetype="dashed", color="red")
plotHL <- qplot(cycle, totalLostFight, data=eHL, geom = "line",
                ylim = c(minFightY,maxFightY),
                main="HL", xlab="", ylab="Wealth") +
  geom_vline(xintercept=iHL, linetype="dashed", color="red")
plotHH <- qplot(cycle, totalLostFight, data=eHH, geom = "line",
                ylim = c(minFightY,maxFightY),
                main="HH", xlab="", ylab="Wealth") +
  geom_vline(xintercept=iHH, linetype="dashed", color="red")
png(file=paste(imagePath,"/lossWealthFight",ext,".png", sep=""),
    width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Loss of Wealth on Fighting")
dev.off()


###
### EXTORTER LOSS OF WEALTH ON PUNISHMENT
###
minPunishY <- floor(min(min(eLL[,totalLostPunishment], eHH[,totalLostPunishment]),
                        min(eLH[,totalLostPunishment], eHL[,totalLostPunishment])))
maxPunishY <- ceiling(max(max(eLL[,totalLostPunishment], eHH[,totalLostPunishment]),
                          max(eLH[,totalLostPunishment], eHL[,totalLostPunishment])))

plotLL <- qplot(cycle, totalLostPunishment, data=eLL, geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="LL", xlab="", ylab="Wealth") +
  geom_vline(xintercept=iLL, linetype="dashed", color="red")
plotLH <- qplot(cycle, totalLostPunishment, data=eLH, geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="LH", xlab="", ylab="Wealth") +
  geom_vline(xintercept=iLH, linetype="dashed", color="red")
plotHL <- qplot(cycle, totalLostPunishment, data=eHL, geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="HL", xlab="", ylab="Wealth") +
  geom_vline(xintercept=iHL, linetype="dashed", color="red")
plotHH <- qplot(cycle, totalLostPunishment, data=eHH, geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="HH", xlab="", ylab="Wealth") +
  geom_vline(xintercept=iHH, linetype="dashed", color="red")
png(file=paste(imagePath,"/lossWealthPunishment",ext,".png", sep=""),
    width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Loss of Wealth on Punishment")
dev.off()


###
### SOCIETY WEALTH LOSSES AND PERCENTAGE
###
eLL <- extorterSum[which(type == 0), ]
eLH <- extorterSum[which(type == 1), ]
eHL <- extorterSum[which(type == 2), ]
eHH <- extorterSum[which(type == 3), ]

if(iLL <= NROW(eLL)){
  eLL <- eLL[iLL:NROW(eLL), wealth := 0]
  eLL <- eLL[iLL:NROW(eLL), totalPunishment := 0]
  eLL <- eLL[iLL:NROW(eLL), totalLostFight := 0]
}
if(iLH <= NROW(eLH)){
  eLH <- eLH[iLH:NROW(eLH), wealth := 0]
  eLH <- eLH[iLH:NROW(eLH), totalPunishment := 0]
  eLH <- eLH[iLH:NROW(eLH), totalLostFight := 0]
}
if(iHL <= NROW(eHL)){
  eHL <- eHL[iHL:NROW(eHL), wealth := 0]
  eHL <- eHL[iHL:NROW(eHL), totalPunishment := 0]
  eHL <- eHL[iHL:NROW(eHL), totalLostFight := 0]
}
if(iHH <= NROW(eHH)){
  eHH <- eHH[iHH:NROW(eHH), wealth := 0]
  eHH <- eHH[iHH:NROW(eHH), totalPunishment := 0]
  eHH <- eHH[iHH:NROW(eHH), totalLostFight := 0]
}

cycle <- seq(1:NROW(targetSum))
dWealth <- mapply(sum, targetSum[,wealth], eLL[,wealth], eLH[,wealth],
                  eHL[,wealth], eHH[,wealth])
dLosses <- mapply(sum, targetSum[,totalPunishment], eLL[,totalLostFight],
                  eLH[,totalLostFight], eHL[,totalLostFight], eHH[,totalLostFight])
society <- cbind(cycle, dWealth, dLosses)
society <- data.table(society)
setnames(society, c("data","wealth","losses"))

minWealthY <- floor(min(society[,wealth]))
maxWealthY <- ceiling(max(society[,wealth]))

png(file=paste(imagePath,"/societyWealth",ext,".png", sep=""),
    width=800, height=600)
qplot(cycle, wealth, data=society, geom = "line", ylim = c(minWealthY,maxWealthY),
      main="Society Wealth", xlab="", ylab="Wealth")
dev.off()

minLossesY <- floor(min(society[,losses]))
maxLossesY <- ceiling(max(society[,losses]))

png(file=paste(imagePath,"/societyLosses",ext,".png", sep=""),
    width=800, height=600)
qplot(cycle, losses, data=society, geom = "line", ylim = c(minLossesY,maxLossesY),
      main="Society Losses", xlab="", ylab="Losses")
dev.off()

minY <- floor(min(society[,losses/(wealth+losses)]))
maxY <- ceiling(max(society[,losses/(wealth+losses)]))

png(file=paste(imagePath,"/societyLossesPercentage",ext,".png", sep=""),
    width=800, height=600)
qplot(cycle, losses/(wealth+losses), data=society, geom = "line", ylim = c(minY,maxY),
      main="Society Losses Percentage", xlab="", ylab="Percentage")
dev.off()