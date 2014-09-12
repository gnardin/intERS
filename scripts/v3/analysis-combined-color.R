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
basePath <- paste("/data/workspace/gloders/intERS/output/v3/",sep="")
dir.create(file.path(basePath, "images"), showWarnings = FALSE)
imagePath <- paste(basePath,"images", sep="")

###
### Constants
###
NC_NP <- 1    ### NO COMPETITION / NO PROTECTION
CP_NP <- 2    ### COMPETITION    / NO PROTECTION
NC_PR <- 3    ### NO COMPETITION / PROTECTION
CP_PR <- 4    ### COMPETITION    / PROTECTION

RANGE <- 1:630

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
#imagePath <- list()


###
### Output path
###
outputPath <- list()
outputPath[[NC_NP]] <- paste(basePath, "01-NCNP", sep="")
outputPath[[CP_NP]] <- paste(basePath, "02-CPNP", sep="")
outputPath[[NC_PR]] <- paste(basePath, "03-NCPR", sep="")
outputPath[[CP_PR]] <- paste(basePath, "04-CPPR", sep="")

for(i in 1:4) {
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
  
  if (i == NC_NP) {
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
  
  if ((i == NC_PR) | (i == CP_NP) | (i == CP_PR)) {
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
### AGGREGATE DATA
###
i <- NC_NP
range <- 1:monopolyCycle[[i]]
type <- rep(i,max(range))
NN <- cbind(type, observerSum[[i]][range,]$cycle,
            # Number of Extorters
            observerSum[[i]][range,]$totalFR+observerSum[[i]][range,]$totalIM,
            # Number of Targets
            observerSum[[i]][range,]$totalTA,
            # Proportion of target's income spent on paying extortion
            ifelse(targetSum[[i]][range,]$income != 0, (targetSum[[i]][range,]$totalExtortionPaid /
                                                          targetSum[[i]][range,]$income) * 100, 0),
            # Proportion of extortion demanded that resulted in punishment
            ifelse(targetSum[[i]][range,]$income != 0, (targetSum[[i]][range,]$numPunishment /
                                                          targetSum[[i]][range,]$numExtortion) * 100, 0),
            # Number of fights among extorters
            extorterSum[[i]][range,]$numCounterattackProtection +
              extorterSum[[i]][range,]$numCounterattackRetaliation,
            # Severity of punishment
            ifelse(targetSum[[i]][range,]$income != 0, (targetSum[[i]][range,]$totalPunishment /
                                                          targetSum[[i]][range,]$income) * 100, 0)
)
NN <- data.table(NN)
setnames(NN,
         c("type", "V2", "V3", "V4", "V5", "V6", "V7", "V8"),
         c("type", "cycle", "numExtorters", "numTargets", "targetsIncome",
           "extortionPunished", "numFights", "sevPunishment"))


i <- CP_NP
range <- 1:monopolyCycle[[i]]
type <- rep(i,max(range))
CN <- cbind(type, observerSum[[i]][range,]$cycle,
            # Number of Extorters
            observerSum[[i]][range,]$totalFR+observerSum[[i]][range,]$totalIM,
            # Number of Targets
            observerSum[[i]][range,]$totalTA,
            # Proportion of target's income spent on paying extortion
            ifelse(targetSum[[i]][range,]$income != 0, (targetSum[[i]][range,]$totalExtortionPaid /
                                                          targetSum[[i]][range,]$income) * 100, 0),
            # Proportion of extortion demanded that resulted in punishment
            ifelse(targetSum[[i]][range,]$income != 0, (targetSum[[i]][range,]$numPunishment /
                                                          targetSum[[i]][range,]$numExtortion) * 100, 0),
            # Number of fights among extorters
            extorterSum[[i]][range,]$numCounterattackProtection +
              extorterSum[[i]][range,]$numCounterattackRetaliation,
            # Severity of punishment
            ifelse(targetSum[[i]][range,]$income != 0, (targetSum[[i]][range,]$totalPunishment /
                                                          targetSum[[i]][range,]$income) * 100, 0)
)
CN <- data.table(CN)
setnames(CN,
         c("type", "V2", "V3", "V4", "V5", "V6", "V7", "V8"),
         c("type", "cycle", "numExtorters", "numTargets", "targetsIncome",
           "extortionPunished", "numFights", "sevPunishment"))


i <- NC_PR
range <- 1:monopolyCycle[[i]]
type <- rep(i,max(range))
NP <- cbind(type, observerSum[[i]][range,]$cycle,
            # Number of Extorters
            observerSum[[i]][range,]$totalFR+observerSum[[i]][range,]$totalIM,
            # Number of Targets
            observerSum[[i]][range,]$totalTA,
            # Proportion of target's income spent on paying extortion
            ifelse(targetSum[[i]][range,]$income != 0, (targetSum[[i]][range,]$totalExtortionPaid /
                                                          targetSum[[i]][range,]$income) * 100, 0),
            # Proportion of extortion demanded that resulted in punishment
            ifelse(targetSum[[i]][range,]$income != 0, (targetSum[[i]][range,]$numPunishment /
                                                          targetSum[[i]][range,]$numExtortion) * 100, 0),
            # Number of fights among extorters
            extorterSum[[i]][range,]$numCounterattackProtection +
              extorterSum[[i]][range,]$numCounterattackRetaliation,
            # Severity of punishment
            ifelse(targetSum[[i]][range,]$income != 0, (targetSum[[i]][range,]$totalPunishment /
                                                          targetSum[[i]][range,]$income) * 100, 0)
)
NP <- data.table(NP)
setnames(NP,
         c("type", "V2", "V3", "V4", "V5", "V6", "V7", "V8"),
         c("type", "cycle", "numExtorters", "numTargets", "targetsIncome",
           "extortionPunished", "numFights", "sevPunishment"))


i <- CP_PR
range <- 1:monopolyCycle[[i]]
type <- rep(i,max(range))
CP <- cbind(type, observerSum[[i]][range,]$cycle,
            # Number of Extorters
            observerSum[[i]][range,]$totalFR+observerSum[[i]][range,]$totalIM,
            # Number of Targets
            observerSum[[i]][range,]$totalTA,
            # Proportion of target's income spent on paying extortion
            ifelse(targetSum[[i]][range,]$income != 0, (targetSum[[i]][range,]$totalExtortionPaid /
                                                          targetSum[[i]][range,]$income) * 100, 0),
            # Proportion of extortion demanded that resulted in punishment
            ifelse(targetSum[[i]][range,]$income != 0, (targetSum[[i]][range,]$numPunishment /
                                                          targetSum[[i]][range,]$numExtortion) * 100, 0),
            # Number of fights among extorters
            extorterSum[[i]][range,]$numCounterattackProtection +
              extorterSum[[i]][range,]$numCounterattackRetaliation,
            # Severity of punishment
            ifelse(targetSum[[i]][range,]$income != 0, (targetSum[[i]][range,]$totalPunishment /
                                                          targetSum[[i]][range,]$income) * 100, 0)
)
CP <- data.table(CP)
setnames(CP,
         c("type", "V2", "V3", "V4", "V5", "V6", "V7", "V8"),
         c("type", "cycle", "numExtorters", "numTargets", "targetsIncome",
           "extortionPunished", "numFights", "sevPunishment"))

minX <- 0
maxX <- max(NN$cycle,CN$cycle,NP$cycle,CP$cycle)

##
## ALL
##
data <- rbind(NN,CN,NP,CP)

##
## NUMBER OF EXTORTERS
##
png(file=paste(imagePath,"/NN-CN-NP-CP-numberExtorters.png", sep=""),
    width=800, height=600)

minY <- 0
maxY <- ceiling(max(NN$numExtorters,CN$numExtorters,
                    NP$numExtorters,CP$numExtorters))

ggplot(data, aes(x = cycle, y = numExtorters,
                 color = factor(type),
                 shape = factor(type),
                 linetype = factor(type))) +
  scale_colour_manual(values=c("black","red","green","blue"),
                      labels=c("NN", "CN", "NP", "CP")) +
  scale_shape_manual(values=c(17,15,16,14),
                     labels=c("NN", "CN", "NP", "CP")) +
  scale_linetype_manual(values=c(1,1,1,1),
                        labels=c("NN", "CN", "NP", "CP")) +
  geom_line(size = 1) + coord_cartesian(ylim=c(minY, maxY + 1)) +
  geom_vline(xintercept = min(monopolyCycle[[NC_NP]], monopolyCycle[[NC_PR]],
                              monopolyCycle[[CP_NP]], monopolyCycle[[CP_PR]]),
             linetype=2, size=1.5, color="black") +
  labs(x = "Round", y = "Number",
       colour = "Treatment",
       shape = "Treatment",
       linetype = "Treatment") +
  theme(axis.text = element_text(size = 16, face = "bold", colour = "black"),
        axis.title = element_text(size = 18, face = "bold"),
        legend.title = element_blank(),
        legend.text = element_text(size=16, face = "bold"),
        legend.position = "top")

dev.off()


##
## NUMBER OF TARGETS
##
png(file=paste(imagePath,"/NN-CN-NP-CP-numberTargets.png", sep=""),
    width=800, height=600)

minY <- 0
maxY <- ceiling(max(NN$numTargets,CN$numTargets,
                    NP$numTargets,CP$numTargets))

ggplot(data, aes(x = cycle, y = numTargets,
                 color = factor(type),
                 shape = factor(type),
                 linetype = factor(type))) +
  scale_colour_manual(values=c("black","red","green","blue"),
                      labels=c("NN", "CN", "NP", "CP")) +
  scale_shape_manual(values=c(17,15,16,14),
                     labels=c("NN", "CN", "NP", "CP")) +
  scale_linetype_manual(values=c(1,1,1,1),
                        labels=c("NN", "CN", "NP", "CP")) +
  geom_line(size = 1) + coord_cartesian(ylim=c(minY, maxY + 1)) +
  geom_vline(xintercept = min(monopolyCycle[[NC_NP]], monopolyCycle[[NC_PR]],
                              monopolyCycle[[CP_NP]], monopolyCycle[[CP_PR]]),
             linetype=2, size=1.5, color="black") +
  labs(x = "Round", y = "Number",
       colour = "Treatment",
       shape = "Treatment",
       linetype = "Treatment") +
  theme(axis.text = element_text(size = 16, face = "bold", colour = "black"),
        axis.title = element_text(size = 18, face = "bold"),
        legend.title = element_blank(),
        legend.text = element_text(size=16, face = "bold"),
        legend.position = "top")

dev.off()


##
## TARGET'S INCOME SPENT ON PAYING EXTORTION
##
png(file=paste(imagePath,"/NN-CN-NP-CP-targetsIncome.png", sep=""),
    width=800, height=600)

minY <- 0
maxY <- ceiling(max(NN$targetsIncome,CN$targetsIncome,
                    NP$targetsIncome,CP$targetsIncome))

ggplot(data, aes(x = cycle, y = targetsIncome,
                 color = factor(type),
                 shape = factor(type),
                 linetype = factor(type))) +
  scale_colour_manual(values=c("black","red","green","blue"),
                      labels=c("NN", "CN", "NP", "CP")) +
  scale_shape_manual(values=c(17,15,16,14),
                     labels=c("NN", "CN", "NP", "CP")) +
  scale_linetype_manual(values=c(1,1,1,1),
                        labels=c("NN", "CN", "NP", "CP")) +
  geom_line(size = 1) + coord_cartesian(ylim=c(minY, 100)) +
  geom_vline(xintercept = min(monopolyCycle[[NC_NP]], monopolyCycle[[NC_PR]],
                              monopolyCycle[[CP_NP]], monopolyCycle[[CP_PR]]),
             linetype=2, size=1.5, color="black") +
  labs(x = "Round", y = "%",
       colour = "Treatment",
       shape = "Treatment",
       linetype = "Treatment") +
  theme(axis.text = element_text(size = 16, face = "bold", colour = "black"),
        axis.title = element_text(size = 18, face = "bold"),
        legend.title = element_blank(),
        legend.text = element_text(size=16, face = "bold"),
        legend.position = "top")

dev.off()


##
## EXTORTIONS THAT RESULTED IN PUNISHMENT
##
png(file=paste(imagePath,"/NN-CN-NP-CP-extortionPunished.png", sep=""),
    width=800, height=600)

minY <- 0
maxY <- ceiling(max(NN$extortionPunished,CN$extortionPunished,
                    NP$extortionPunished,CP$extortionPunished))

ggplot(data, aes(x = cycle, y = extortionPunished,
                 color = factor(type),
                 shape = factor(type),
                 linetype = factor(type))) +
  scale_colour_manual(values=c("black","red","green","blue"),
                      labels=c("NN", "CN", "NP", "CP")) +
  scale_shape_manual(values=c(17,15,16,14),
                     labels=c("NN", "CN", "NP", "CP")) +
  scale_linetype_manual(values=c(1,1,1,1),
                        labels=c("NN", "CN", "NP", "CP")) +
  geom_line(size = 1) + coord_cartesian(ylim=c(minY, 100)) +
  geom_vline(xintercept = min(monopolyCycle[[NC_NP]], monopolyCycle[[NC_PR]],
                              monopolyCycle[[CP_NP]], monopolyCycle[[CP_PR]]),
             linetype=2, size=1.5, color="black") +
  labs(x = "Round", y = "%",
       colour = "Treatment",
       shape = "Treatment",
       linetype = "Treatment") +
  theme(axis.text = element_text(size = 16, face = "bold", colour = "black"),
        axis.title = element_text(size = 18, face = "bold"),
        legend.title = element_blank(),
        legend.text = element_text(size=16, face = "bold"),
        legend.position = "top")

dev.off()


##
## NUMBER OF FIGHTS
##
png(file=paste(imagePath,"/NN-CN-NP-CP-numberFights.png", sep=""),
    width=800, height=600)

minY <- 0
maxY <- ceiling(max(NN$numFights,CN$numFights,
                    NP$numFights,CP$numFights))

ggplot(data, aes(x = cycle, y = numFights,
                 color = factor(type),
                 shape = factor(type),
                 linetype = factor(type))) +
  scale_colour_manual(values=c("black","red","green","blue"),
                      labels=c("NN", "CN", "NP", "CP")) +
  scale_shape_manual(values=c(17,15,16,14),
                     labels=c("NN", "CN", "NP", "CP")) +
  scale_linetype_manual(values=c(1,1,1,1),
                        labels=c("NN", "CN", "NP", "CP")) +
  geom_line(size = 1) + coord_cartesian(ylim=c(minY, maxY + 1)) +
  geom_vline(xintercept = min(monopolyCycle[[NC_NP]], monopolyCycle[[NC_PR]],
                              monopolyCycle[[CP_NP]], monopolyCycle[[CP_PR]]),
             linetype=2, size=1.5, color="black") +
  labs(x = "Round", y = "Number",
       colour = "Treatment",
       shape = "Treatment",
       linetype = "Treatment") +
  theme(axis.text = element_text(size = 16, face = "bold", colour = "black"),
        axis.title = element_text(size = 18, face = "bold"),
        legend.title = element_blank(),
        legend.text = element_text(size=16, face = "bold"),
        legend.position = "top")

dev.off()


##
## SEVERITY OF PUNISHMENT
##
png(file=paste(imagePath,"/NN-CN-NP-CP-sevPunishment.png", sep=""),
    width=800, height=600)

minY <- 0
maxY <- ceiling(max(NN$sevPunishment,CN$sevPunishment,
                    NP$sevPunishment,CP$sevPunishment))

ggplot(data, aes(x = cycle, y = sevPunishment,
                 color = factor(type),
                 shape = factor(type),
                 linetype = factor(type))) +
  scale_colour_manual(values=c("black","red","green","blue"),
                      labels=c("NN", "CN", "NP", "CP")) +
  scale_shape_manual(values=c(17,15,16,14),
                     labels=c("NN", "CN", "NP", "CP")) +
  scale_linetype_manual(values=c(1,1,1,1),
                        labels=c("NN", "CN", "NP", "CP")) +
  geom_line(size = 1) + coord_cartesian(ylim=c(minY, maxY + 1)) +
  geom_vline(xintercept = min(monopolyCycle[[NC_NP]], monopolyCycle[[NC_PR]],
                              monopolyCycle[[CP_NP]], monopolyCycle[[CP_PR]]),
             linetype=2, size=1.5, color="black") +
  labs(x = "Round", y = "Severity",
       colour = "Treatment",
       shape = "Treatment",
       linetype = "Treatment") +
  theme(axis.text = element_text(size = 16, face = "bold", colour = "black"),
        axis.title = element_text(size = 18, face = "bold"),
        legend.title = element_blank(),
        legend.text = element_text(size=16, face = "bold"),
        legend.position = "top")

dev.off()