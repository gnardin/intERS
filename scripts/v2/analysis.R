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
v <- as.numeric(args[1])
i <- as.numeric(args[2])

###
### Raw simulation data output directory
###
basePath <- paste("/data/workspace/gloders/intERS/output/everyone/",sep="")


###
### Constants
###
NO_COMPETITION <- 1
NO_PROTECTION <- 2
PROTECTION <- 3
  
RANGE <- 1:700
  
CYCLES <- 300
SIMULATIONS <- 50
EXTORTERS <- 20
  
###
### Global Variables
###
extorterSum <- list()
extorterAvg <- list()
observerSum <- list()
observerAvg <- list()
targetSum <- list()
targetAvg <- list()
monopolyCycle <- list()
imagePath <- list()


###
### Output path
###
outputPath <- list()
outputPath[[NO_COMPETITION]] <- paste(basePath, "noCompetition", sep="")
outputPath[[NO_PROTECTION]] <- paste(basePath, "noProtection", sep="")
outputPath[[PROTECTION]] <- paste(basePath, "protection", sep="")
  
for(i in 1:3) {
  dir.create(file.path(outputPath[[i]], "images"), showWarnings = FALSE)
  imagePath[[i]] <- paste(outputPath[[i]],"/images", sep="")
  
  ###
  ### Upload Consolidated Information
  ###
  extorterSum[[i]] <- data.table(
    read.csv(paste(outputPath[[i]],"/cSumextorters.csv",sep=""),sep=";"))
  setkey(extorterSum[[i]], type, cycle)
  
  extorterAvg[[i]] <- data.table(
    read.csv(paste(outputPath[[i]],"/cAvgextorters.csv",sep=""),sep=";"))
  setkey(extorterAvg[[i]], type, cycle)
  
  observerSum[[i]] <- data.table(
    read.csv(paste(outputPath[[i]],"/","cSumobservers.csv",sep=""), sep=";"))
  setkey(observerSum[[i]], type, cycle)

  observerAvg[[i]] <- data.table(
    read.csv(paste(outputPath[[i]],"/","cAvgobservers.csv",sep=""), sep=";"))
  setkey(observerAvg[[i]], type, cycle)

  targetSum[[i]] <- data.table(
    read.csv(paste(outputPath[[i]],"/","cSumtargets.csv",sep=""), sep=";"))
  setkey(targetSum[[i]], type, cycle)

  targetAvg[[i]] <- data.table(
    read.csv(paste(outputPath[[i]],"/","cAvgtargets.csv",sep=""), sep=";"))
  setkey(targetAvg[[i]], type, cycle)


  ###
  ### GLOBAL VARIABLES
  ###
  monopolyCycle[[i]] <- min(observerSum[[i]][which((totalFR+totalIM) <= 1),cycle],
                            nrow(observerSum[[i]])+1)

  if (i == 1) {
    for(x in (monopolyCycle[[i]] + 1):max(RANGE)) {
      observerAvg[[i]][x, extortion := 0]
      observerAvg[[i]][x, punishment := 0]
    
      observerSum[[i]][x, totalTS := 0]
      observerSum[[i]][x, totalFR := 0]
      observerSum[[i]][x, totalIM := 0]
    
      targetSum[[i]][x, numExtortion := 0]
      targetSum[[i]][x, totalExtortionPaid := 0]
      targetSum[[i]][x, numExtortionPaid := 0]
      targetSum[[i]][x, totalPunishment := 0]
      targetSum[[i]][x, numPunishment := 0]
      
      extorterSum[[i]][x, CounterattackProtection := 0]
      extorterSum[[i]][x, CounterattackRetaliation := 0]
    }
  }

  if (i == 2) {
    observerAvg[[i]] <- observerAvg[[i]][which(cycle <= monopolyCycle[[i]])]
    observerSum[[i]] <- observerSum[[i]][which(cycle <= monopolyCycle[[i]])]
    targetSum[[i]] <- targetSum[[i]][which(cycle <= monopolyCycle[[i]])]
  
    for(x in (monopolyCycle[[i]] + 1):max(RANGE)) {
      observerAvg[[i]] <- rbind(observerAvg[[i]], observerAvg[[i]][
        which(cycle == monopolyCycle[[i]]),])
      observerAvg[[i]][x + 1, cycle := as.integer(x)]
      
      observerSum[[i]] <- rbind(observerSum[[i]], observerSum[[i]][
        which(cycle == monopolyCycle[[i]]),])
      observerSum[[i]][x + 1, cycle := as.integer(x)]
      
      targetSum[[i]] <- rbind(targetSum[[i]], targetSum[[i]][
        which(cycle == monopolyCycle[[i]]),])
      targetSum[[i]][x + 1, cycle := as.integer(x)]
    }
  }
}

###
### SUMMARY
###
eWealth <- matrix(0, nrow=3, ncol=SIMULATIONS)
tWealth <- matrix(0, nrow=3, ncol=SIMULATIONS)
nTargets <- matrix(0, nrow=3, ncol=SIMULATIONS)
for(i in 2:3) {
  for(sim in 1:SIMULATIONS) {
    extorter <- data.table(
      read.csv(paste(outputPath[[i]],"/",sim,"/","extorters.csv",sep=""), sep=";",header=TRUE))
    eWealth[i,sim] <- extorter[which(cycle == monopolyCycle[2]),]$wealth
    
    target <- data.table(
      read.csv(paste(outputPath[[i]],"/",sim,"/","targets.csv",sep=""), sep=";",header=TRUE))
    tWealth[i,sim] <- sum(target[which(cycle == monopolyCycle[2]),]$wealth)
    nTargets[i,sim] <- length(target[which(cycle == monopolyCycle[2]),]$wealth)
  }
}

###
### NUMBER OF TARGETS
###
minY <- 0
maxY <- ceiling(max(observerSum[[i]][,totalTS]))

png(file=paste(imagePath[[i]],"/numberTargets.png", sep=""), width=800, height=600)
qplot(cycle, totalTS, data=observerSum[[i]], geom = "line", size = I(1),
      ylim = c(minY, maxY), main="", xlab="Round", ylab="Number") +
  geom_vline(xintercept=monopolyCycle[[i]], linetype="dashed", color="red")+
  theme(axis.text=element_text(size=16,face="bold",colour="black"),
        axis.title=element_text(size=18,face="bold"))
dev.off()


###
### NUMBER OF TARGETS (RANGE)
###
minY <- 0
maxY <- ceiling(max(observerSum[[i]][RANGE,totalTS]))

png(file=paste(imagePath[[i]],"/numberTargets_RANGE.png", sep=""), width=800, height=600)
qplot(cycle, totalTS, data=observerSum[[i]][RANGE,], geom = "line", size = I(1),
      ylim = c(minY, maxY), main="", xlab="Round", ylab="Number") +
  geom_vline(xintercept=monopolyCycle[[i]], linetype="dashed", color="red") +
  theme(axis.text=element_text(size=16,face="bold",colour="black"),
        axis.title=element_text(size=18,face="bold")) +
  geom_text(aes(500,1,label="Monopoly"))
dev.off()

# Same graphic
type <- rep(2,max(RANGE))
targetCNP <- cbind(type, observerSum[[2]][RANGE,]$cycle, observerSum[[2]][RANGE,]$totalTS)
type <- rep(3,max(RANGE))
targetCP  <- cbind(type, observerSum[[3]][RANGE,]$cycle, observerSum[[3]][RANGE,]$totalTS)

data <- rbind(targetCNP, targetCP)
data <- data.table(data)
data$type <- as.numeric(data$type)
data$V2 <- as.numeric(data$V2)
data$V3 <- as.numeric(data$V3)
setnames(data,
         c("type", "V2", "V3"),
         c("type", "cycle", "numTargets"))

ggplot(data, aes(x = cycle, y = numTargets,
                    color = factor(type),
                    shape = factor(type),
                    linetype = factor(type)
                 )) +
  scale_colour_manual(values=c("black","black"),
                      labels=c("Competition & No-Protection", "Competition & Protection")) +
  scale_shape_manual(values=c(17,15),
                     labels=c("Competition & No-Protection", "Competition & Protection")) +
  scale_linetype_manual(values=c(2,1),
                        labels=c("Competition & No-Protection", "Competition & Protection")) +
  geom_line() + geom_jitter(size=1.9) + coord_cartesian(ylim=c(0,2000), xlim=c(0,300)) +
  geom_vline(xintercept=min(monopolyCycle[[2]],monopolyCycle[[3]]), linetype="dotted", color="black") +
  labs(x = "Round", y = "Number of Targets",
       colour = "Treatment",
       shape = "Treatment",
       linetype = "Treatment")


###
### NUMBER OF TOTAL EXTORTERS
###
minY <- 0
maxY <- ceiling(max(observerSum[[i]][,totalFR+totalIM]))

png(file=paste(imagePath[[i]],"/numberTotalExtorters.png", sep=""),
    width=800, height=600)
qplot(cycle, totalFR+totalIM, data=observerSum[[i]], geom = "line", size = I(1),
      ylim = c(minY, maxY),
      main="", xlab="Round", ylab="Number") +
  geom_vline(xintercept=monopolyCycle[[i]], linetype="dashed", color="red") +
  theme(axis.text=element_text(size=16,face="bold",colour="black"),
        axis.title=element_text(size=18,face="bold"))
dev.off()


###
### NUMBER OF TOTAL EXTORTERS (RANGE)
###
minY <- 0
maxY <- ceiling(max(observerSum[[i]][RANGE,totalFR+totalIM]))

png(file=paste(imagePath[[i]],"/numberTotalExtorters_RANGE.png", sep=""),
    width=800, height=600)
qplot(cycle, totalFR+totalIM, data=observerSum[[i]][RANGE,], geom = "line",
      size = I(1), ylim = c(minY, maxY), main="", xlab="Round", ylab="Number") +
  geom_vline(xintercept=monopolyCycle[[i]], linetype="dashed", color="red") +
  theme(axis.text=element_text(size=16,face="bold",colour="black"),
        axis.title=element_text(size=18,face="bold"))
dev.off()


###
### NUMBER OF EXTORTERS PER TARGET BEING EXTORTED
###
minY <- 0
maxY <- ceiling(max(targetSum[[i]][,numExtortion / extorted], na.rm = TRUE))

png(file=paste(imagePath[[i]],"/numberExtortersPerTarget.png", sep=""),
    width=800, height=600)
qplot(cycle, ifelse(extorted != 0, numExtortion / extorted, 0),
      data=targetSum[[i]], geom = "line", size = I(1),
      ylim = c(minY, maxY), main="", xlab="Round", ylab="Number") +
  geom_vline(xintercept=monopolyCycle[[i]], linetype="dashed", color="red") +
  theme(axis.text=element_text(size=16,face="bold",colour="black"),
        axis.title=element_text(size=18,face="bold"))
dev.off()


###
### NUMBER OF EXTORTERS PER TARGET BEING EXTORTED (RANGE)
###
minY <- 0
maxY <- ceiling(max(targetSum[[i]][RANGE,numExtortion / extorted], na.rm = TRUE))

png(file=paste(imagePath[[i]],"/numberExtortersPerTarget_RANGE.png", sep=""),
    width=800, height=600)
qplot(cycle, ifelse(extorted != 0, numExtortion / extorted, 0),
      data=targetSum[[i]][RANGE,], geom = "line", size = I(1),
      ylim = c(minY, maxY), main="", xlab="Round", ylab="Number") +
  geom_vline(xintercept=monopolyCycle[[i]], linetype="dashed", color="red") +
  theme(axis.text=element_text(size=16,face="bold",colour="black"),
        axis.title=element_text(size=18,face="bold"))
dev.off()


###
### AMOUNT OF EXTORTIONS PAID X INCOME
###
minY <- 0
maxY <- 100

png(file=paste(imagePath[[i]],"/percAmountExtortionXIncome.png", sep=""),
    width=800, height=600)
qplot(cycle, ifelse(income != 0, (totalExtortionPaid / income) * 100, 0),
      data=targetSum[[i]], geom = "line", size = I(1),
      ylim = c(minY, maxY), main="", xlab="Round", ylab="%") +
  geom_vline(xintercept=monopolyCycle[[i]], linetype="dashed", color="red") +
  theme(axis.text=element_text(size=16,face="bold",colour="black"),
        axis.title=element_text(size=18,face="bold"))
dev.off()


###
### AMOUNT OF EXTORTIONS PAID X INCOME (RANGE)
###
minY <- 0
maxY <- 100

png(file=paste(imagePath[[i]],"/percAmountExtortionXIncome_RANGE.png", sep=""),
    width=800, height=600)
qplot(cycle, ifelse(income != 0, (totalExtortionPaid / income) * 100, 0),
      data=targetSum[[i]][RANGE,], geom = "line", size = I(1),
      ylim = c(minY, maxY), main="", xlab="Round", ylab="%") +
  geom_vline(xintercept=monopolyCycle[[i]], linetype="dashed", color="red") +
  theme(axis.text=element_text(size=16,face="bold",colour="black"),
        axis.title=element_text(size=18,face="bold"))
dev.off()


###
### AMOUNT OF PAID EXTORTION
###
minY <- 0
maxY <- ceiling(max(targetSum[[i]][,totalExtortionPaid / totalExtortion], na.rm = TRUE)) * 100

png(file=paste(imagePath[[i]],"/percAmountExtortionsPaid.png", sep=""),
    width=800, height=600)
qplot(cycle, ifelse(totalExtortion != 0, (totalExtortionPaid / totalExtortion) * 100, 0),
      data=targetSum[[i]], geom = "line", size = I(1),
      ylim = c(minY, maxY), main="", xlab="Round", ylab="%") +
  geom_vline(xintercept=monopolyCycle[[i]], linetype="dashed", color="red") +
  theme(axis.text=element_text(size=16,face="bold",colour="black"),
        axis.title=element_text(size=18,face="bold"))
dev.off()


###
### AMOUNT OF PAID EXTORTION (RANGE)
###
minY <- 0
maxY <- ceiling(max(targetSum[[i]][RANGE,totalExtortionPaid / totalExtortion], na.rm = TRUE)) * 100

png(file=paste(imagePath[[i]],"/percAmountExtortionsPaid_RANGE.png", sep=""),
    width=800, height=600)
qplot(cycle, ifelse(totalExtortion != 0, (totalExtortionPaid / totalExtortion) * 100, 0),
      data=targetSum[[i]][RANGE,], geom = "line", size = I(1),
      ylim = c(minY, maxY), main="", xlab="Round", ylab="%") +
  geom_vline(xintercept=monopolyCycle[[i]], linetype="dashed", color="red") +
  theme(axis.text=element_text(size=16,face="bold",colour="black"),
        axis.title=element_text(size=18,face="bold"))
dev.off()


###
### NUMBER OF PAID EXTORTIONS
###
minY <- 0
maxY <- ceiling(max(targetSum[[i]][,numExtortionPaid / numExtortion], na.rm = TRUE)) * 100

png(file=paste(imagePath[[i]],"/percNumberExtortionsPaid.png", sep=""),
    width=800, height=600)
qplot(cycle, ifelse(numExtortion != 0, (numExtortionPaid / numExtortion) * 100, 0),
      data=targetSum[[i]], geom = "line", size = I(1),
      ylim = c(minY, maxY), main="", xlab="Round", ylab="%") +
  geom_vline(xintercept=monopolyCycle[[i]], linetype="dashed", color="red") +
  theme(axis.text=element_text(size=16,face="bold",colour="black"),
        axis.title=element_text(size=18,face="bold"))
dev.off()


###
### NUMBER OF PAID EXTORTIONS (RANGE)
###
minY <- 0
maxY <- ceiling(max(targetSum[[i]][RANGE,numExtortionPaid / numExtortion], na.rm = TRUE)) * 100

png(file=paste(imagePath[[i]],"/percNumberExtortionsPaid_RANGE.png", sep=""),
    width=800, height=600)
qplot(cycle, ifelse(numExtortion != 0, (numExtortionPaid / numExtortion) * 100, 0),
      data=targetSum[[i]][RANGE,], geom = "line", size = I(1),
      ylim = c(minY, maxY), main="", xlab="Round", ylab="%") +
  geom_vline(xintercept=monopolyCycle[[i]], linetype="dashed", color="red") +
  theme(axis.text=element_text(size=16,face="bold",colour="black"),
        axis.title=element_text(size=18,face="bold"))
dev.off()


###
### AMOUNT OF PUNISHMENT
###
minY <- 0
maxY <- ceiling(max(targetSum[[i]][,totalPunishment / income], na.rm = TRUE)) * 100

png(file=paste(imagePath[[i]],"/percAmountPunishmentXIncome.png", sep=""),
    width=800, height=600)
qplot(cycle, ifelse(income != 0, (totalPunishment / income) * 100, 0),
      data=targetSum[[i]], geom = "line", size = I(1),
      ylim = c(minY, maxY), main="", xlab="Round", ylab="%") +
  geom_vline(xintercept=monopolyCycle[[i]], linetype="dashed", color="red") +
  theme(axis.text=element_text(size=16,face="bold",colour="black"),
        axis.title=element_text(size=18,face="bold"))
dev.off()


###
### AMOUNT OF PUNISHMENT (RANGE)
###
minY <- 0
maxY <- ceiling(max(targetSum[[i]][RANGE,totalPunishment / income], na.rm = TRUE)) * 100

png(file=paste(imagePath[[i]],"/percAmountPunishmentXIncome_RANGE.png", sep=""),
    width=800, height=600)
qplot(cycle, ifelse(income != 0, (totalPunishment / income) * 100, 0),
      data=targetSum[[i]][RANGE,], geom = "line", size = I(1),
      ylim = c(minY, maxY), main="", xlab="Round", ylab="%") +
  geom_vline(xintercept=monopolyCycle[[i]], linetype="dashed", color="red") +
  theme(axis.text=element_text(size=16,face="bold",colour="black"),
        axis.title=element_text(size=18,face="bold"))
dev.off()


###
### NUMBER OF PUNISHMENTS PER EXTORTION
###
minY <- 0
maxY <- ceiling(max(targetSum[[i]][,numPunishment / numExtortion], na.rm = TRUE)) * 100

png(file=paste(imagePath[[i]],"/percNumberPunishmentPerExtortion.png", sep=""),
    width=800, height=600)
qplot(cycle, ifelse(income != 0, (numPunishment / numExtortion) * 100, 0),
      data=targetSum[[i]], geom = "line", size = I(1),
      ylim = c(minY, maxY), main="", xlab="Round", ylab="%") +
  geom_vline(xintercept=monopolyCycle[[i]], linetype="dashed", color="red") +
  theme(axis.text=element_text(size=16,face="bold",colour="black"),
        axis.title=element_text(size=18,face="bold"))
dev.off()


###
### NUMBER OF PUNISHMENTS PER EXTORTION (RANGE)
###
minY <- 0
maxY <- ceiling(max(targetSum[[i]][RANGE,numPunishment / numExtortion], na.rm = TRUE)) * 100

png(file=paste(imagePath[[i]],"/percNumberPunishmentPerExtortion_RANGE.png", sep=""),
    width=800, height=600)
qplot(cycle, ifelse(income != 0, (numPunishment / numExtortion) * 100, 0),
      data=targetSum[[i]][RANGE,], geom = "line", size = I(1),
      ylim = c(minY, maxY), main="", xlab="Round", ylab="%") +
  geom_vline(xintercept=monopolyCycle[[i]], linetype="dashed", color="red") +
  theme(axis.text=element_text(size=16,face="bold",colour="black"),
        axis.title=element_text(size=18,face="bold"))
dev.off()


###
### AMOUNT OF FIGHTING
###
minY <- 0
maxY <- ceiling(max(extorterSum[[i]]
                    [,(totalLostFightProtection + totalLostFightRetaliation)
                     / wealth], na.rm = TRUE)) * 100

png(file=paste(imagePath[[i]],"/percAmountFights.png", sep=""),
    width=800, height=600)
qplot(cycle, ifelse(wealth != 0,
                    ((totalLostFightProtection + totalLostFightRetaliation)
                    / wealth) * 100, 0),
      data=extorterSum[[i]], geom = "line", size = I(1),
      ylim = c(minY, maxY), main="", xlab="Round", ylab="%") +
  geom_vline(xintercept=monopolyCycle[[i]], linetype="dashed", color="red") +
  theme(axis.text=element_text(size=16,face="bold",colour="black"),
        axis.title=element_text(size=18,face="bold"))
dev.off()


###
### AMOUNT OF FIGHT (RANGE)
###
minY <- 0
maxY <- ceiling(max(extorterSum[[i]]
                    [RANGE,(totalLostFightProtection + totalLostFightRetaliation)
                     / wealth], na.rm = TRUE)) * 100

png(file=paste(imagePath[[i]],"/percAmountFights_RANGE.png", sep=""),
    width=800, height=600)
qplot(cycle, ifelse(wealth != 0,
                    ((totalLostFightProtection + totalLostFightRetaliation)
                    / wealth) * 100, 0),
      data=extorterSum[[i]][RANGE,], geom = "line", size = I(1),
      ylim = c(minY, maxY), main="", xlab="Round", ylab="%") +
  geom_vline(xintercept=monopolyCycle[[i]], linetype="dashed", color="red") +
  theme(axis.text=element_text(size=16,face="bold",colour="black"),
        axis.title=element_text(size=18,face="bold"))
dev.off()


###
### NUMBER OF FIGHTING
###
minY <- 0
maxY <- ceiling(max(extorterSum[[i]]
                    [,numCounterattackProtection + numCounterattackRetaliation],
                    na.rm = TRUE))

png(file=paste(imagePath[[i]],"/numberFights.png", sep=""),
    width=800, height=600)
qplot(cycle, numCounterattackProtection + numCounterattackRetaliation,
      data=extorterSum[[i]], geom = "line", size = I(1),
      ylim = c(minY, maxY), main="", xlab="Round", ylab="Number") +
  geom_vline(xintercept=monopolyCycle[[i]], linetype="dashed", color="red") +
  theme(axis.text=element_text(size=16,face="bold",colour="black"),
        axis.title=element_text(size=18,face="bold"))
dev.off()


###
### NUMBER OF FIGHT (RANGE)
###
minY <- 0
maxY <- ceiling(max(extorterSum[[i]]
                    [RANGE,numCounterattackProtection + numCounterattackRetaliation],
                    na.rm = TRUE))

png(file=paste(imagePath[[i]],"/numberFights_RANGE.png", sep=""),
    width=800, height=600)
qplot(cycle, numCounterattackProtection + numCounterattackRetaliation,
      data=extorterSum[[i]][RANGE,], geom = "line", size = I(1),
      ylim = c(minY, maxY), main="", xlab="Round", ylab="Number") +
  geom_vline(xintercept=monopolyCycle[[i]], linetype="dashed", color="red") +
  theme(axis.text=element_text(size=16,face="bold",colour="black"),
        axis.title=element_text(size=18,face="bold"))
dev.off()


###
### PERCENT OF EXTORTION RATE
###
minY <- 0
maxY <- 100

png(file=paste(imagePath[[i]],"/percExtortion.png", sep=""),
    width=800, height=600)
qplot(cycle, extortion * 100, data=extorterAvg[[i]], geom = "line", size = I(1),
      ylim = c(minY, maxY), main="", xlab="Round", ylab="%") +
  geom_vline(xintercept=monopolyCycle[[i]], linetype="dashed", color="red") +
  theme(axis.text=element_text(size=16,face="bold",colour="black"),
        axis.title=element_text(size=18,face="bold"))
dev.off()


###
### PERCENT OF EXTORTION RATE (RANGE)
###
minY <- 0
maxY <- 100

png(file=paste(imagePath[[i]],"/percExtortion_RANGE.png", sep=""),
    width=800, height=600)
qplot(cycle, extortion * 100, data=extorterAvg[[i]][RANGE,], geom = "line", size = I(1),
      ylim = c(minY, maxY), main="", xlab="Round", ylab="% Extortion") +
  geom_vline(xintercept=monopolyCycle[[i]], linetype="dashed", color="red") +
  theme(axis.text=element_text(size=16,face="bold",colour="black"),
        axis.title=element_text(size=18,face="bold"))
dev.off()


###
### PERCENT OF PUNISHMENT RATE
###
minY <- 0
maxY <- 100

png(file=paste(imagePath[[i]],"/percPunishment.png", sep=""),
    width=800, height=600)
qplot(cycle, punishment * 100, data=extorterAvg[[i]], geom = "line", size = I(1),
      ylim = c(minY, maxY), main="", xlab="Round", ylab="%") +
  geom_vline(xintercept=monopolyCycle[[i]], linetype="dashed", color="red") +
  theme(axis.text=element_text(size=16,face="bold",colour="black"),
        axis.title=element_text(size=18,face="bold"))
dev.off()


###
### PERCENT OF PUNISHMENT RATE (RANGE)
###
minY <- 0
maxY <- 100

png(file=paste(imagePath[[i]],"/percPunishment_RANGE.png", sep=""),
    width=800, height=600)
qplot(cycle, punishment * 100, data=extorterAvg[[i]][RANGE,], geom = "line", size = I(1),
      ylim = c(minY, maxY), main="", xlab="Round", ylab="%") +
  geom_vline(xintercept=monopolyCycle[[i]], linetype="dashed", color="red") +
  theme(axis.text=element_text(size=16,face="bold",colour="black"),
        axis.title=element_text(size=18,face="bold"))
dev.off()


###
### SIZE OF THE DOMAINS
###
extorters <- matrix(0, nrow=CYCLES, ncol=EXTORTERS)
for(sim in 1:SIMULATIONS) {
  temp <- data.table(read.csv(paste(outputPath[[i]],"/",sim,"/extorters.csv",sep=""),sep=";"))
  setkey(temp, type, cycle)
  
  for(c in 1:CYCLES) {
    for(extorter in 1:EXTORTERS) {
      value <- as.integer(temp[which(id == extorter & cycle == c), numTargets])
      if (length(value) > 0) {
        extorters[c,extorter] <- extorters[c,extorter] + value
      }
    }
  }
}

for(c in 1:CYCLES) {
  for(extorter in 1:EXTORTERS) {
    extorters[c,extorter] <- extorters[c,extorter] / SIMULATIONS
  }
}

for(x in seq(1,100,5)) {
data <- NULL
for(extorter in 1:EXTORTERS) {
  data <- rbind(data, temp[which(cycle == 1 & id == extorter),
                           c(id,extortion,punishment, extorters[x,extorter])])
}
data <- data.frame(data)
names(data) <- c("id","extortion","punishment","value")
symbols(data$extortion * 100, data$punishment * 100, circles=sqrt((data$value / 50)),
        inches = FALSE, fg="white", bg="black",
        xlab="Extortion Level (%)", ylab="Punishment Severity (%)")
}


###
### NUMBER OF PUNISHMENTS
###
punishments <- matrix(0, nrow=CYCLES, ncol=EXTORTERS)
extortions <- matrix(0, nrow=CYCLES, ncol=EXTORTERS)
punext <- matrix(0, nrow=CYCLES, ncol=EXTORTERS)
extorterLife <- matrix(0, nrow=SIMULATIONS, ncol=EXTORTERS)
for(sim in 1:SIMULATIONS) {
  temp <- data.table(read.csv(paste(outputPath[[i]],"/",sim,"/extorters.csv",sep=""),sep=";"))
  setkey(temp, type, cycle)
  
  for(c in 1:CYCLES) {
    for(extorter in 1:EXTORTERS) {
      valuePun <- as.integer(temp[which(id == extorter & cycle == c), numPunishment])
      valueExt <- as.integer(temp[which(id == extorter & cycle == c), numExtortionDemanded])
      if (length(valuePun) > 0) {
        punishments[c,extorter] <- punishments[c,extorter] + valuePun
        extortions[c,extorter] <- extortions[c,extorter] + valueExt
        punext[c,extorter] <- punext[c,extorter] + (valuePun / valueExt)
      } else {
        if(extorterLife[sim,extorter] == 0) {
          extorterLife[sim,extorter] <- c
        }
      }
    }
  }
}

for(l in 1:20) {
  print(c(l," ",mean(extorterLife[,l])))
}

# No-Protection sequence
for(l in c(14,6,5,15,4,13,3,17,12,2,11,1,20,19,18,16,10,9,8,7)){
  k <- 0
  for(s in 1:SIMULATIONS){
    k <- k + sum(punext[,l]) / extorterLife[s,l]
  }
  print(k / SIMULATIONS)
}

k <- 0
l <- 14
for(s in 1:SIMULATIONS){
  k <- k + sum(punext[,l]) / 88
}
print(k / SIMULATIONS)

# Protection
for(l in c(14,13,15,6,4,3,12,5,2,11,17,1,20,19,18,16,10,9,8,7)){
  k <- 0
  for(s in 1:SIMULATIONS){
    k <- k + sum(punext[,l]) / extorterLife[s,l]
  }
  print(k / SIMULATIONS)
}

k <- 0
l <- 14
for(s in 1:SIMULATIONS){
  k <- k + sum(punext[,l]) / 88
}
print(k / SIMULATIONS)