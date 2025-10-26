;FACTS ASSERTED BY GAME INPUT
(deftemplate BLINKY
	(slot edible (type SYMBOL))
	(slot nearToNotEdibleGhost (type SYMBOL))
	(slot nearToPacman (type SYMBOL))
	(slot distToPacman (type NUMBER))
    (slot distToPacmanJunction (type NUMBER))
    (slot distToPacmanPowerPill (type NUMBER))
)	
(deftemplate INKY
	(slot edible (type SYMBOL))
	(slot nearToNotEdibleGhost (type SYMBOL))
	(slot nearToPacman (type SYMBOL))
	(slot distToPacman (type NUMBER))
    (slot distToPacmanJunction (type NUMBER))
    (slot distToPacmanPowerPill (type NUMBER))
)	
	
(deftemplate PINKY
	(slot edible (type SYMBOL))
	(slot nearToNotEdibleGhost (type SYMBOL))
	(slot nearToPacman (type SYMBOL))
	(slot distToPacman (type NUMBER))
    (slot distToPacmanJunction (type NUMBER))
    (slot distToPacmanPowerPill (type NUMBER))
)

(deftemplate SUE
	(slot edible (type SYMBOL))
	(slot nearToNotEdibleGhost (type SYMBOL))
	(slot nearToPacman (type SYMBOL))
	(slot distToPacman (type NUMBER))
    (slot distToPacmanJunction (type NUMBER))
    (slot distToPacmanPowerPill (type NUMBER))
)


(deftemplate MSPACMAN 
    (slot nearToPowerPill (type SYMBOL))
)
    
(deftemplate GAME
    (slot onlyOnePowerPillLeft (type SYMBOL))
)

;DEFINITION OF THE ACTION FACT
(deftemplate ACTION
	(slot id)
	(slot info (default ""))
	(slot priority (type NUMBER)) ; mandatory slots
	(slot runawaystrategy (type SYMBOL)) ; Extra slot for the runaway action
	(slot chasestrategy (type SYMBOL)) ; Extra slot for the chase action
) 

;RULES 

(defrule SUErunsAwayMSPACMANclosePPill
	(MSPACMAN (nearToPowerPill true))
	=>  
	(assert 
		(ACTION 
			(id SUErunsAway)
			(info "MSPacMan cerca PPill")
			(priority 50)
			(runawaystrategy POWERPILL)
		)
	)
)

(defrule SUEchasesNotEdibleGhost
	(SUE (edible true) (nearToNotEdibleGhost true))
	=>  
	(assert 
		(ACTION 
			(id SUEchases)
			(info "Comestible --> huir hacia fantasma no comestible")
			(priority 40)
			(chasestrategy GHOST)
		)
	)
)

(defrule SUErunsAwayPacman
	(SUE (edible true) (nearToPacman true))
	=>  
	(assert 
		(ACTION 
			(id SUErunsAway)
			(info "Comestible --> huir")
			(priority 30)
			(runawaystrategy PACMAN)
		)
	)
)

(defrule SUEchasesPowerPill
    (SUE (edible false))
    (GAME (onlyOnePowerPillLeft true))
    => 
    (assert 
    	(ACTION 
			(id SUEchases)
			(info "No comestible y solo queda una PP --> perseguir PowerPill")
			(priority 15)
    		(chasestrategy CIRCLE_POWERPILL)
    	)
    )
)


(defrule SUEchasesPacman
    (SUE (edible false)
             (distToPacman ?dp)
             (distToPacmanJunction ?dj)
             (distToPacmanPowerPill ?dpill))
    (test (< ?dp ?dj))
    (test (< ?dp ?dpill))
    =>
    (assert (ACTION (id SUEchases)
                    (info "Pacman es el más cercano")
                    (priority 20)
                    (chasestrategy PACMAN)))
)

(defrule SUEchasesJunction
    (SUE (edible false)
             (distToPacman ?dp)
             (distToPacmanJunction ?dj)
             (distToPacmanPowerPill ?dpill))
    (test (< ?dj ?dp))
    (test (< ?dj ?dpill))
    =>
    (assert (ACTION (id SUEchases)
                    (info "Junction más cercana")
                    (priority 20)
                    (chasestrategy JUNCTION)))
)

(defrule SUEchasesPill
    (SUE (edible false)
             (distToPacman ?dp)
             (distToPacmanJunction ?dj)
             (distToPacmanPowerPill ?dpill))
    (test (< ?dpill ?dp))
    (test (< ?dpill ?dj))
    =>
    (assert (ACTION (id SUEchases)
                    (info "Pill más cercana")
                    (priority 20)
                    (chasestrategy PILL)))
)