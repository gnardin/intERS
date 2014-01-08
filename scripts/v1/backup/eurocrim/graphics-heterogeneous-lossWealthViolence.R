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
en <- 10
to <- 40
ex <- 10
pu <- 40

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
### EXTORTER LOSS OF WEALTH ON VIOLENCE
###
eLL <- extorterSum[which(type == 0), ]
eLH <- extorterSum[which(type == 1), ]
eHL <- extorterSum[which(type == 2), ]
eHH <- extorterSum[which(type == 3), ]

lastLine <- max(max(nrow(eLL),nrow(eHH)),max(nrow(eLH),nrow(eHL)))

for(index in 2:lastLine){
  
  if(index < nrow(eLL)){
    aux <- eLL[index-1,totalLostFight] + eLL[index,totalLostFight]
    eLL[index,totalLostFight := aux]
    aux <- eLL[index-1,totalLostPunishment] + eLL[index,totalLostPunishment]
    eLL[index,totalLostPunishment := aux]
    aux <- eLL[index-1,totalExtortionReceived] + eLL[index,totalExtortionReceived]
    eLL[index,totalExtortionReceived := aux]
  }
  
  if(index < nrow(eLH)){
    aux <- eLH[index-1,totalLostFight] + eLH[index,totalLostFight]
    eLH[index,totalLostFight := aux]
    aux <- eLH[index-1,totalLostPunishment] + eLH[index,totalLostPunishment]
    eLH[index,totalLostPunishment := aux]
    aux <- eLH[index-1,totalExtortionReceived] + eLH[index,totalExtortionReceived]
    eLH[index,totalExtortionReceived := aux]
  }
  
  if(index < nrow(eHL)){
    aux <- eHL[index-1,totalLostFight] + eHL[index,totalLostFight]
    eHL[index,totalLostFight := aux]
    aux <- eHL[index-1,totalLostPunishment] + eHL[index,totalLostPunishment]
    eHL[index,totalLostPunishment := aux]
    aux <- eHL[index-1,totalExtortionReceived] + eHL[index,totalExtortionReceived]
    eHL[index,totalExtortionReceived := aux]
  }
  
  if(index < nrow(eHH)){
    aux <- eHH[index-1,totalLostFight] + eHH[index,totalLostFight]
    eHH[index,totalLostFight := aux]
    aux <- eHH[index-1,totalLostPunishment] + eHH[index,totalLostPunishment]
    eHH[index,totalLostPunishment := aux]
    aux <- eHH[index-1,totalExtortionReceived] + eHH[index,totalExtortionReceived]
    eHH[index,totalExtortionReceived := aux]
  }
}

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

minViolenceY <- floor(min(min(eLL[,totalLostFight+totalLostPunishment],
                              eHH[,totalLostFight+totalLostPunishment]),
                          min(eLH[,totalLostFight+totalLostPunishment],
                              eHL[,totalLostFight+totalLostPunishment])))
maxViolenceY <- ceiling(max(max(eLL[,totalLostFight+totalLostPunishment],
                                eHH[,totalLostFight+totalLostPunishment]),
                            max(eLH[,totalLostFight+totalLostPunishment],
                                eHL[,totalLostFight+totalLostPunishment])))

data <- data.table(type="LL",cycle=eLL$cycle,totalLostFight=eLL$totalLostFight,
                   totalLostPunishment=eLL$totalLostPunishment,totalExtortionReceived=eLL$totalExtortionReceived)
data <- rbind(data, data.table(type="LH",cycle=eLH$cycle,totalLostFight=eLH$totalLostFight,
                   totalLostPunishment=eLH$totalLostPunishment,totalExtortionReceived=eLH$totalExtortionReceived))
data <- rbind(data, data.table(type="HL",cycle=eHL$cycle,totalLostFight=eHL$totalLostFight,
                               totalLostPunishment=eHL$totalLostPunishment,totalExtortionReceived=eHL$totalExtortionReceived))
data <- rbind(data, data.table(type="HH",cycle=eHH$cycle,totalLostFight=eHH$totalLostFight,
                               totalLostPunishment=eHH$totalLostPunishment,totalExtortionReceived=eHH$totalExtortionReceived))

ggplot(data, aes(cycle, fill=factor(type), colour=factor(type))) +
  geom_line(aes(y=((totalLostFight+totalLostPunishment)/totalExtortionReceived)))

nLL <- eLL[1:iLL-1][totalExtortionReceived > 0,]
nLH <- eLH[1:iLH-1][totalExtortionReceived > 0,]
nHL <- eHL[1:iHL-1][totalExtortionReceived > 0,]
nHH <- eHH[1:iHH-1][totalExtortionReceived > 0,]

data <- data.table(type="LL",
                   value=mean((nLL$totalLostFight+nLL$totalLostPunishment)/nLL$totalExtortionReceived))
data <- rbind(data, data.table(type="LH",
                               value=mean((nLH$totalLostFight+nLH$totalLostPunishment)/nLH$totalExtortionReceived)))
data <- rbind(data, data.table(type="HL",
                               value=mean((nHL$totalLostFight+nHL$totalLostPunishment)/nHL$totalExtortionReceived)))
data <- rbind(data, data.table(type="HH",
                               value=mean((nHH$totalLostFight+nHH$totalLostPunishment)/nHH$totalExtortionReceived)))

ggplot(data, aes(factor(type, levels=c("LL","LH","HL","HH")),  y=value*100)) +
  ylim(0,100) +
  labs(title = "") + xlab("Extorter Policy") + ylab("% Loss Wealth Violence") +
  geom_bar(width=0.5) +
  geom_text(aes(label=formatC(value*100, digits=3), vjust=0))


fight <- data.table(type="LL",
                   value=mean((nLL$totalLostFight)/nLL$totalExtortionReceived))
fight <- rbind(fight, data.table(type="LH",
                               value=mean((nLH$totalLostFight)/nLH$totalExtortionReceived)))
fight <- rbind(fight, data.table(type="HL",
                               value=mean((nHL$totalLostFight)/nHL$totalExtortionReceived)))
fight <- rbind(fight, data.table(type="HH",
                               value=mean((nHH$totalLostFight)/nHH$totalExtortionReceived)))

ggplot(fight, aes(factor(type, levels=c("LL","LH","HL","HH")),  y=value*100)) +
  ylim(0,100) +
  labs(title = "") + xlab("Extorter Policy") + ylab("% Loss Wealth Fight") +
  geom_bar(width=0.5) +
  geom_text(aes(label=formatC(value*100, digits=3), vjust=0))

punish <- data.table(type="LL",
                    value=mean((nLL$totalLostPunishment)/nLL$totalExtortionReceived))
punish <- rbind(punish, data.table(type="LH",
                                 value=mean((nLH$totalLostPunishment)/nLH$totalExtortionReceived)))
punish <- rbind(punish, data.table(type="HL",
                                 value=mean((nHL$totalLostPunishment)/nHL$totalExtortionReceived)))
punish <- rbind(punish, data.table(type="HH",
                                 value=mean((nHH$totalLostPunishment)/nHH$totalExtortionReceived)))

ggplot(punish, aes(factor(type, levels=c("LL","LH","HL","HH")),  y=value*100)) +
  ylim(0,100) +
  labs(title = "") + xlab("Extorter Policy") + ylab("% Loss Wealth Punishment") +
  geom_bar(width=0.5) +
  geom_text(aes(label=formatC(value*100, digits=3), vjust=0))