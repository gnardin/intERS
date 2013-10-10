###
### Required libraries
###
library(data.table)
library(ggplot2)
library(gridExtra)

rm(list=ls())
###
### Raw simulation data output directory
###
basePath <- "/data/workspace/gloders/intERS/output"

outputPath <- paste(basePath, "/no_attack",sep="")
        
###
### Images repository directory
###
dir.create(file.path(outputPath, "images"), showWarnings = FALSE)
imagePath <- paste(outputPath,"/images", sep="")

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

png(file=paste(imagePath,"/numberTargets.png", sep=""), width=800, height=600)
qplot(cycle, totalTargets, data=observerSum, geom = "line",
      ylim = c(minTotalTargetsY, maxTotalTargetsY),
      main="Number of Targets", xlab="", ylab="Number")
dev.off()


###
### NUMBER OF TARGETS MINIMUM EQUAL 0
###
minTotalTargetsY <- 0
maxTotalTargetsY <- ceiling(max(observerSum[,totalTargets]))

png(file=paste(imagePath,"/numberTargetsMin0.png", sep=""), width=800, height=600)
qplot(cycle, totalTargets, data=observerSum, geom = "line",
      ylim = c(minTotalTargetsY, maxTotalTargetsY),
      main="Number of Targets", xlab="", ylab="Number")
dev.off()


###
### TARGETS ACCUMULATED WEALTH
####
minWealthY <- floor(min(targetSum[,wealth]))
maxWealthY <- ceiling(max(targetSum[,wealth]))

png(file=paste(imagePath,"/accumulatedTargetsWealth.png", sep=""), width=800, height=600)
qplot(cycle, wealth, data=targetSum, geom = "line",
      ylim = c(minWealthY,maxWealthY),
      main="Targets Accumulated Wealth", xlab="", ylab="Wealth")
dev.off()


###
### ALL TARGETS AVERAGE INCOME
###
minIncomeY <- floor(min(targetAvg[,income]))
maxIncomeY <- ceiling(max(targetAvg[,income]))

png(file=paste(imagePath,"/averageAllTargetsIncome.png", sep=""), width=800, height=600)
qplot(cycle, income, data=targetAvg, geom = "line",
      ylim = c(minIncomeY, maxIncomeY),
      main="All Targets Average Income", xlab="", ylab="Wealth")
dev.off()


###
### AVERAGE PERCENTAGE OF TARGETS INCOME PAID ON EXTORTION
###
minIncomeY <- floor(min(targetAvg[,totalPaid] / 
                          targetAvg[,income+totalPaid+totalPunishment]))
maxIncomeY <- ceiling(max(targetAvg[,totalPaid] /
                            targetAvg[,income+totalPaid+totalPunishment]))

png(file=paste(imagePath,"/averageAllTargetsExtortedIncome.png", sep=""),
    width=800, height=600)
qplot(cycle, totalPaid/(income+totalPaid+totalPunishment), data=targetAvg, geom = "line",
      ylim = c(minIncomeY,maxIncomeY),
      main="Average Targets Income Percentage Paid on Extortion",
      xlab="", ylab="Percentage")
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

png(file=paste(imagePath,"/numberExtortersTotal.png", sep=""), width=800, height=600)
qplot(cycle, totalExtortersFree+totalExtortersImprisoned, data=o, geom = "line",
      ylim = c(minTotalExtortersY,maxTotalExtortersY),
      main="Total Number of Extorters", xlab="", ylab="Number")
dev.off()


###
### NUMBER OF EXTORTERS PER TARGET BEING EXTORTED
###
minExtortersPerTargetY <- 0
maxExtortersPerTargetY <- ceiling(max(observerSum[,NE0/TE0], na.rm = TRUE))

png(file=paste(imagePath,"/numberExtortersPerTarget.png", sep=""), width=800, height=600)
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
png(file=paste(imagePath,"/numberExtortersType.png", sep=""), width=800, height=600)
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
png(file=paste(imagePath,"/accumulatedExtortersWealth.png", sep=""),
    width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Extorters Accumulated Wealth")
dev.off()


###
### ALL EXTORTERS AVERAGE WEALTH
###
eLL <- extorterAvg[which(type == 0),]
eLH <- extorterAvg[which(type == 1),]
eHL <- extorterAvg[which(type == 2),]
eHH <- extorterAvg[which(type == 3),]

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

minWealthY <- floor(min(min(eLL[, wealth],eHH[, wealth]),
                        min(eLH[, wealth],eHL[, wealth])))
maxWealthY <- ceiling(max(max(eLL[, wealth],eHH[, wealth]),
                          max(eLH[, wealth],eHL[, wealth])))

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
png(file=paste(imagePath,"/averageAllExtortersWealth.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Extorters Average Wealth")
dev.off()


###
### NUMBER OF EXTORTIONS
###
eLL <- extorterSum[which(type == 0), ]
eLH <- extorterSum[which(type == 1), ]
eHL <- extorterSum[which(type == 2), ]
eHH <- extorterSum[which(type == 3), ]

if(iLL <= NROW(eLL)){
  eLL <- eLL[iLL:NROW(eLL), numExtortion := 0]
}
if(iLH <= NROW(eLH)){
  eLH <- eLH[iLH:NROW(eLH), numExtortion := 0]
}
if(iHL <= NROW(eHL)){
  eHL <- eHL[iHL:NROW(eHL), numExtortion := 0]
}
if(iHH <= NROW(eHH)){
  eHH <- eHH[iHH:NROW(eHH), numExtortion := 0]
}

minNumExtortionY <- 0
maxNumExtortionY <- ceiling(max(max(eLL[,numExtortion],eHH[,numExtortion]),
                                max(eLH[,numExtortion],eHL[,numExtortion])))

plotLL <- qplot(cycle, numExtortion, data=eLL, geom = "line",
                ylim = c(minNumExtortionY,maxNumExtortionY),
                main="LL", xlab="", ylab="Number") +
  geom_vline(xintercept=iLL, linetype="dashed", color="red")
plotLH <- qplot(cycle, numExtortion, data=eLH, geom = "line",
                ylim = c(minNumExtortionY,maxNumExtortionY),
                main="LH", xlab="", ylab="Number") +
  geom_vline(xintercept=iLH, linetype="dashed", color="red")
plotHL <- qplot(cycle, numExtortion, data=eHL, geom = "line",
                ylim = c(minNumExtortionY,maxNumExtortionY),
                main="HL", xlab="", ylab="Number") +
  geom_vline(xintercept=iHL, linetype="dashed", color="red")
plotHH <- qplot(cycle, numExtortion, data=eHH, geom = "line",
                ylim = c(minNumExtortionY,maxNumExtortionY),
                main="HH", xlab="", ylab="Number") +
  geom_vline(xintercept=iHH, linetype="dashed", color="red")
png(file=paste(imagePath,"/numberExtortions.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Number of Extortions")
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
png(file=paste(imagePath,"/numberTargetsPerExtorter.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Number of Targets per Extorter")
dev.off()


###
### EXTORTER VIOLENCE
###
eLL <- extorterSum[which(type == 0), ]
eLH <- extorterSum[which(type == 1), ]
eHL <- extorterSum[which(type == 2), ]
eHH <- extorterSum[which(type == 3), ]

if(iLL <= NROW(eLL)){
  eLL <- eLL[iLL:NROW(eLL), numCounterattack := 0]
  eLL <- eLL[iLL:NROW(eLL), numPunishment := 0]
}
if(iLH <= NROW(eLH)){
  eLH <- eLH[iLH:NROW(eLH), numCounterattack := 0]
  eLH <- eLH[iLH:NROW(eLH), numPunishment := 0]
}
if(iHL <= NROW(eHL)){
  eHL <- eHL[iHL:NROW(eHL), numCounterattack := 0]
  eHL <- eHL[iHL:NROW(eHL), numPunishment := 0]
}
if(iHH <= NROW(eHH)){
  eHH <- eHH[iHH:NROW(eHH), numCounterattack := 0]
  eHH <- eHH[iHH:NROW(eHH), numPunishment := 0]
}

minViolenceY <- floor(min(min(eLL[,numCounterattack+numPunishment],
                              eHH[,numCounterattack+numPunishment]),
                          min(eLH[,numCounterattack+numPunishment],
                              eHL[,numCounterattack+numPunishment])))
maxViolenceY <- ceiling(max(max(eLL[,numCounterattack+numPunishment],
                                eHH[,numCounterattack+numPunishment]),
                            max(eLH[,numCounterattack+numPunishment],
                                eHL[,numCounterattack+numPunishment])))

plotLL <- qplot(cycle, numCounterattack+numPunishment, data=eLL, geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="LL", xlab="", ylab="Number of Fights + Punishments") +
  geom_vline(xintercept=iLL, linetype="dashed", color="red")
plotLH <- qplot(cycle, numCounterattack+numPunishment, data=eLH, geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="LH", xlab="", ylab="Number of Fights + Punishments") +
  geom_vline(xintercept=iLH, linetype="dashed", color="red")
plotHL <- qplot(cycle, numCounterattack+numPunishment, data=eHL, geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="HL", xlab="", ylab="Number of Fights + Punishments") +
  geom_vline(xintercept=iHL, linetype="dashed", color="red")
plotHH <- qplot(cycle, numCounterattack+numPunishment, data=eHH, geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="HH", xlab="", ylab="Number of Fights + Punishments") +
  geom_vline(xintercept=iHH, linetype="dashed", color="red")
png(file=paste(imagePath,"/numberViolence.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Extorters Number of Violent Activities")
dev.off()


###
### EXTORTER VIOLENCE HIGHLIGHT 1:100
###
minViolenceY <- 0
maxViolenceY <- ceiling(max(max(eLL[1:100,numCounterattack+numPunishment],
                                eHH[1:100,numCounterattack+numPunishment]),
                            max(eLH[1:100,numCounterattack+numPunishment],
                                eHL[1:100,numCounterattack+numPunishment])))

plotLL <- qplot(cycle, numCounterattack+numPunishment, data=eLL[1:100], geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="LL", xlab="", ylab="Number of Fights + Punishments")
plotLH <- qplot(cycle, numCounterattack+numPunishment, data=eLH[1:100], geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="LH", xlab="", ylab="Number of Fights + Punishments")
plotHL <- qplot(cycle, numCounterattack+numPunishment, data=eHL[1:100], geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="HL", xlab="", ylab="Number of Fights + Punishments")
plotHH <- qplot(cycle, numCounterattack+numPunishment, data=eHH[1:100], geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="HH", xlab="", ylab="Number of Fights + Punishments")
png(file=paste(imagePath,"/numberViolenceHighlighted:1-100.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Number of Violent Activities [100]")
dev.off()


###
### EXTORTER VIOLENCE HIGHLIGHT 100:END
###
minViolenceY <- 0
maxViolenceY <- ceiling(max(max(eLL[100:NROW(eLL),numCounterattack+numPunishment],
                                eHH[100:NROW(eHH),numCounterattack+numPunishment]),
                            max(eLH[100:NROW(eLH),numCounterattack+numPunishment],
                                eHL[100:NROW(eHL),numCounterattack+numPunishment])))

plotLL <- qplot(cycle, numCounterattack+numPunishment, data=eLL[100:NROW(eLL)], geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="LL", xlab="", ylab="Number of Fights + Punishments")
plotLH <- qplot(cycle, numCounterattack+numPunishment, data=eLH[100:NROW(eLH)], geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="LH", xlab="", ylab="Number of Fights + Punishments")
plotHL <- qplot(cycle, numCounterattack+numPunishment, data=eHL[100:NROW(eHL)], geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="HL", xlab="", ylab="Number of Fights + Punishments")
plotHH <- qplot(cycle, numCounterattack+numPunishment, data=eHH[100:NROW(eHH)], geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="HH", xlab="", ylab="Number of Fights + Punishments")
png(file=paste(imagePath,"/numberViolenceHighlighted:100-END.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Number of Violent Activities [100:END]")
dev.off()


###
### EXTORTER LOSS OF WEALTH ON VIOLENCE
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

minViolenceY <- floor(min(min(eLL[,totalLostFight+totalLostPunishment],
                              eHH[,totalLostFight+totalLostPunishment]),
                          min(eLH[,totalLostFight+totalLostPunishment],
                              eHL[,totalLostFight+totalLostPunishment])))
maxViolenceY <- ceiling(max(max(eLL[,totalLostFight+totalLostPunishment],
                                eHH[,totalLostFight+totalLostPunishment]),
                            max(eLH[,totalLostFight+totalLostPunishment],
                                eHL[,totalLostFight+totalLostPunishment])))

plotLL <- qplot(cycle, totalLostFight+totalLostPunishment, data=eLL, geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="LL", xlab="", ylab="Wealth") +
  geom_vline(xintercept=iLL, linetype="dashed", color="red")
plotLH <- qplot(cycle, totalLostFight+totalLostPunishment, data=eLH, geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="LH", xlab="", ylab="Wealth") +
  geom_vline(xintercept=iLH, linetype="dashed", color="red")
plotHL <- qplot(cycle, totalLostFight+totalLostPunishment, data=eHL, geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="HL", xlab="", ylab="Wealth") +
  geom_vline(xintercept=iHL, linetype="dashed", color="red")
plotHH <- qplot(cycle, totalLostFight+totalLostPunishment, data=eHH, geom = "line",
                ylim = c(minViolenceY,maxViolenceY),
                main="HH", xlab="", ylab="Wealth") +
  geom_vline(xintercept=iHH, linetype="dashed", color="red")
png(file=paste(imagePath,"/lossWealthViolence.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Loss of Wealth on Violence")
dev.off()


###
### EXTORTER LOSS OF WEALTH ON FIGHTING
###
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
png(file=paste(imagePath,"/lossWealthFight.png", sep=""), width=800, height=600)
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
png(file=paste(imagePath,"/lossWealthPunishment.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Loss of Wealth on Punishment")
dev.off()


###
### EXTORTER PUNISHMENT
###
eLL <- extorterSum[which(type == 0), ]
eLH <- extorterSum[which(type == 1), ]
eHL <- extorterSum[which(type == 2), ]
eHH <- extorterSum[which(type == 3), ]

if(iLL <= NROW(eLL)){
  eLL <- eLL[iLL:NROW(eLL), numPunishment := 0]
}
if(iLH <= NROW(eLH)){
  eLH <- eLH[iLH:NROW(eLH), numPunishment := 0]
}
if(iHL <= NROW(eHL)){
  eHL <- eHL[iHL:NROW(eHL), numPunishment := 0]
}
if(iHH <= NROW(eHH)){
  eHH <- eHH[iHH:NROW(eHH), numPunishment := 0]
}

minPunishY <- floor(min(min(eLL[,numPunishment], eHH[,numPunishment]),
                        min(eLH[,numPunishment], eHL[,numPunishment])))
maxPunishY <- ceiling(max(max(eLL[,numPunishment], eHH[,numPunishment]),
                          max(eLH[,numPunishment], eHL[,numPunishment])))

plotLL <- qplot(cycle, numPunishment, data=eLL, geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="LL", xlab="", ylab="Number of Punishments") +
  geom_vline(xintercept=iLL, linetype="dashed", color="red")
plotLH <- qplot(cycle, numPunishment, data=eLH, geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="LH", xlab="", ylab="Number of Punishments") +
  geom_vline(xintercept=iLH, linetype="dashed", color="red")
plotHL <- qplot(cycle, numPunishment, data=eHL, geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="HL", xlab="", ylab="Number of Punishments") +
  geom_vline(xintercept=iHL, linetype="dashed", color="red")
plotHH <- qplot(cycle, numPunishment, data=eHH, geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="HH", xlab="", ylab="Number of Punishments") +
  geom_vline(xintercept=iHH, linetype="dashed", color="red")
png(file=paste(imagePath,"/numberPunishment.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Extorters Number of Punishment")
dev.off()


###
### EXTORTER PUNISHMENT HIGHLIGHT 1:100
###
minPunishY <- 0
maxPunishY <- ceiling(max(max(eLL[1:100,numPunishment], eHH[1:100,numPunishment]),
                          max(eLH[1:100,numPunishment], eHL[1:100,numPunishment])))

plotLL <- qplot(cycle, numPunishment, data=eLL[1:100], geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="LL", xlab="", ylab="Number of Punishments")
plotLH <- qplot(cycle, numPunishment, data=eLH[1:100], geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="LH", xlab="", ylab="Number of Punishments")
plotHL <- qplot(cycle, numPunishment, data=eHL[1:100], geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="HL", xlab="", ylab="Number of Punishments")
plotHH <- qplot(cycle, numPunishment, data=eHH[1:100], geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="HH", xlab="", ylab="Number of Punishments")
png(file=paste(imagePath,"/numberPunishmentHighlighted:1-100.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Extorters Number of Punishments [1:100]")
dev.off()


###
### EXTORTER PUNISHMENT HIGHLIGHT 100:END
###
minPunishY <- 0
maxPunishY <- ceiling(max(max(eLL[100:NROW(eLL),numPunishment],
                              eHH[100:NROW(eHH),numPunishment]),
                          max(eLH[100:NROW(eLH),numPunishment],
                              eHL[100:NROW(eHL),numPunishment])))

plotLL <- qplot(cycle, numPunishment, data=eLL[100:NROW(eLL)], geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="LL", xlab="", ylab="Number of Punishments")
plotLH <- qplot(cycle, numPunishment, data=eLH[100:NROW(eLH)], geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="LH", xlab="", ylab="Number of Punishments")
plotHL <- qplot(cycle, numPunishment, data=eHL[100:NROW(eHL)], geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="HL", xlab="", ylab="Number of Punishments")
plotHH <- qplot(cycle, numPunishment, data=eHH[100:NROW(eHH)], geom = "line",
                ylim = c(minPunishY,maxPunishY),
                main="HH", xlab="", ylab="Number of Punishments")
png(file=paste(imagePath,"/numberPunishmentHighlighted:100-END.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Extorters Number of Punishments [100:END]")
dev.off()


###
### EXTORTER FIGHT
###
eLL <- extorterSum[which(type == 0), ]
eLH <- extorterSum[which(type == 1), ]
eHL <- extorterSum[which(type == 2), ]
eHH <- extorterSum[which(type == 3), ]

if(iLL <= NROW(eLL)){
  eLL <- eLL[iLL:NROW(eLL), numCounterattack := 0]
  eLL <- eLL[iLL:NROW(eLL), numReceivedCounterattack := 0]
}
if(iLH <= NROW(eLH)){
  eLH <- eLH[iLH:NROW(eLH), numCounterattack := 0]
  eLH <- eLH[iLH:NROW(eLH), numReceivedCounterattack := 0]
}
if(iHL <= NROW(eHL)){
  eHL <- eHL[iHL:NROW(eHL), numCounterattack := 0]
  eHL <- eHL[iHL:NROW(eHL), numReceivedCounterattack := 0]
}
if(iHH <= NROW(eHH)){
  eHH <- eHH[iHH:NROW(eHH), numCounterattack := 0]
  eHH <- eHH[iHH:NROW(eHH), numReceivedCounterattack := 0]
}

minFightY <- floor(min(min(eLL[,numCounterattack+numReceivedCounterattack],
                           eHH[,numCounterattack+numReceivedCounterattack]),
                       min(eLH[,numCounterattack+numReceivedCounterattack],
                           eHL[,numCounterattack+numReceivedCounterattack])))
maxFightY <- ceiling(max(max(eLL[,numCounterattack+numReceivedCounterattack],
                             eHH[,numCounterattack+numReceivedCounterattack]),
                         max(eLH[,numCounterattack+numReceivedCounterattack],
                             eHL[,numCounterattack+numReceivedCounterattack])))

plotLL <- qplot(cycle, numCounterattack+numReceivedCounterattack, data=eLL, geom = "line",
                ylim = c(minFightY,maxFightY),
                main="LL", xlab="", ylab="Number of Fights") +
  geom_vline(xintercept=iLL, linetype="dashed", color="red")
plotLH <- qplot(cycle, numCounterattack+numReceivedCounterattack, data=eLH, geom = "line",
                ylim = c(minFightY,maxFightY),
                main="LH", xlab="", ylab="Number of Fights") +
  geom_vline(xintercept=iLH, linetype="dashed", color="red")
plotHL <- qplot(cycle, numCounterattack+numReceivedCounterattack, data=eHL, geom = "line",
                ylim = c(minFightY,maxFightY),
                main="HL", xlab="", ylab="Number of Fights") +
  geom_vline(xintercept=iHL, linetype="dashed", color="red")
plotHH <- qplot(cycle, numCounterattack+numReceivedCounterattack, data=eHH, geom = "line",
                ylim = c(minFightY,maxFightY),
                main="HH", xlab="", ylab="Number of Fights") +
  geom_vline(xintercept=iHH, linetype="dashed", color="red")
png(file=paste(imagePath,"/numberFight.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Extorters Number of Fights")
dev.off()


###
### EXTORTER FIGHT HIGHLIGHT 1:100
###
minFightY <- 0
maxFightY <- ceiling(max(max(eLL[1:100,numCounterattack+numReceivedCounterattack],
                             eHH[1:100,numCounterattack+numReceivedCounterattack]),
                         max(eLH[1:100,numCounterattack+numReceivedCounterattack],
                             eHL[1:100,numCounterattack+numReceivedCounterattack])))

plotLL <- qplot(cycle, numCounterattack+numReceivedCounterattack, data=eLL[1:100], geom = "line",
                ylim = c(minFightY,maxFightY),
                main="LL", xlab="", ylab="Number of Fights")
plotLH <- qplot(cycle, numCounterattack+numReceivedCounterattack, data=eLH[1:100], geom = "line",
                ylim = c(minFightY,maxFightY),
                main="LH", xlab="", ylab="Number of Fights")
plotHL <- qplot(cycle, numCounterattack+numReceivedCounterattack, data=eHL[1:100], geom = "line",
                ylim = c(minFightY,maxFightY),
                main="HL", xlab="", ylab="Number of Fights")
plotHH <- qplot(cycle, numCounterattack+numReceivedCounterattack, data=eHH[1:100], geom = "line",
                ylim = c(minFightY,maxFightY),
                main="HH", xlab="", ylab="Number of Fights")
png(file=paste(imagePath,"/numberFightHighlighted:1-100.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Extorters Number of Fights [1:100]")
dev.off()


###
### EXTORTER FIGHT HIGHLIGHT 100:END
###
minFightY <- 0
maxFightY <- ceiling(max(max(eLL[100:NROW(eLL),numCounterattack+numReceivedCounterattack],
                             eHH[100:NROW(eHH),numCounterattack+numReceivedCounterattack]),
                         max(eLH[100:NROW(eLH),numCounterattack+numReceivedCounterattack],
                             eHL[100:NROW(eHL),numCounterattack+numReceivedCounterattack])))

plotLL <- qplot(cycle, numCounterattack+numReceivedCounterattack, data=eLL[100:NROW(eLL)], geom = "line",
                ylim = c(minFightY,maxFightY),
                main="LL", xlab="", ylab="Number of Fights")
plotLH <- qplot(cycle, numCounterattack+numReceivedCounterattack, data=eLH[100:NROW(eLH)], geom = "line",
                ylim = c(minFightY,maxFightY),
                main="LH", xlab="", ylab="Number of Fights")
plotHL <- qplot(cycle, numCounterattack+numReceivedCounterattack, data=eHL[100:NROW(eHL)], geom = "line",
                ylim = c(minFightY,maxFightY),
                main="HL", xlab="", ylab="Number of Fights")
plotHH <- qplot(cycle, numCounterattack+numReceivedCounterattack, data=eHH[100:NROW(eHH)], geom = "line",
                ylim = c(minFightY,maxFightY),
                main="HH", xlab="", ylab="Number of Fights")
png(file=paste(imagePath,"/numberFightHighlighted:100-END.png", sep=""), width=800, height=600)
grid.arrange(plotLL, plotLH, plotHL, plotHH, main="Extorters Number of Fights [100:END]")
dev.off()


###
### NUMBER OF PAID EXTORTIONS
###
minNumPaidY <- 0
maxNumPaidY <- ceiling(max(targetSum[,numPaid]))

png(file=paste(imagePath,"/numberExtortionsPaid.png", sep=""), width=800, height=600)
qplot(cycle, numPaid, data=targetSum, geom = "line", ylim = c(minNumPaidY,maxNumPaidY),
      main="Extortions Paid", xlab="", ylab="Number")
dev.off()


###
### NUMBER OF PAID EXTORTIONS 1-250
###
minNumPaidY <- 0
maxNumPaidY <- ceiling(max(targetSum[1:250,numPaid]))

png(file=paste(imagePath,"/numberExtortionsPaid:1-250.png", sep=""), width=800, height=600)
qplot(cycle, numPaid, data=targetSum[1:250,], geom = "line",
      ylim = c(minNumPaidY,maxNumPaidY),
      main="Extortions Paid", xlab="", ylab="Number")
dev.off()


###
### NUMBER OF NON PAID EXTORTIONS
###
minNumNotPaidY <- 0
maxNumNotPaidY <- ceiling(max(targetSum[,numNotPaid]))

png(file=paste(imagePath,"/numberExtortionsNotPaid.png", sep=""), width=800, height=600)
qplot(cycle, numNotPaid, data=targetSum, geom = "line",
      ylim = c(minNumNotPaidY,maxNumNotPaidY),
      main="Extortions Not Paid", xlab="", ylab="Number")
dev.off()


###
### NUMBER OF NON PAID EXTORTIONS 1-250
###
minNumNotPaidY <- 0
maxNumNotPaidY <- ceiling(max(targetSum[1:250,numNotPaid]))

png(file=paste(imagePath,"/numberExtortionsNotPaid:1-250.png", sep=""),
    width=800, height=600)
qplot(cycle, numNotPaid, data=targetSum[1:250,], geom = "line",
      ylim = c(minNumNotPaidY,maxNumNotPaidY),
      main="Extortions Not Paid", xlab="", ylab="Number")
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

png(file=paste(imagePath,"/societyWealth.png", sep=""), width=800, height=600)
qplot(cycle, wealth, data=society, geom = "line", ylim = c(minWealthY,maxWealthY),
      main="Society Wealth", xlab="", ylab="Wealth")
dev.off()

minLossesY <- floor(min(society[,losses]))
maxLossesY <- ceiling(max(society[,losses]))

png(file=paste(imagePath,"/societyLosses.png", sep=""), width=800, height=600)
qplot(cycle, losses, data=society, geom = "line", ylim = c(minLossesY,maxLossesY),
      main="Society Losses", xlab="", ylab="Losses")
dev.off()

minY <- floor(min(society[,losses/(wealth+losses)]))
maxY <- ceiling(max(society[,losses/(wealth+losses)]))

png(file=paste(imagePath,"/societyLossesPercentage.png", sep=""), width=800, height=600)
qplot(cycle, losses/(wealth+losses), data=society, geom = "line", ylim = c(minY,maxY),
      main="Society Losses Percentage", xlab="", ylab="Percentage")
dev.off()