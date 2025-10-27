;FACTS ASSERTED BY GAME INPUT
(deftemplate BLINKY
	(slot edible (type SYMBOL))
	(slot nearToNotEdibleGhost (type SYMBOL))
	(slot nearToEdibleGhost (type SYMBOL))
	(slot nearToPacman (type SYMBOL))
	(slot distToPacman (type NUMBER))
    (slot distToPacmanJunction (type NUMBER))
    (slot distToPacmanPowerPill (type NUMBER))
	(slot ghostInPowerPill (type SYMBOL))
)	
(deftemplate INKY
	(slot edible (type SYMBOL))
	(slot nearToNotEdibleGhost (type SYMBOL))
	(slot nearToEdibleGhost (type SYMBOL))
	(slot nearToPacman (type SYMBOL))
	(slot distToPacman (type NUMBER))
    (slot distToPacmanJunction (type NUMBER))
    (slot distToPacmanPowerPill (type NUMBER))
	(slot ghostInPowerPill (type SYMBOL))
)	
	
(deftemplate PINKY
	(slot edible (type SYMBOL))
	(slot nearToNotEdibleGhost (type SYMBOL))
	(slot nearToEdibleGhost (type SYMBOL))
	(slot nearToPacman (type SYMBOL))
	(slot distToPacman (type NUMBER))
    (slot distToPacmanJunction (type NUMBER))
    (slot distToPacmanPowerPill (type NUMBER))
	(slot ghostInPowerPill (type SYMBOL))
)

(deftemplate SUE
	(slot edible (type SYMBOL))
	(slot nearToNotEdibleGhost (type SYMBOL))
	(slot nearToEdibleGhost (type SYMBOL))
	(slot nearToPacman (type SYMBOL))
	(slot distToPacman (type NUMBER))
    (slot distToPacmanJunction (type NUMBER))
    (slot distToPacmanPowerPill (type NUMBER))
	(slot ghostInPowerPill (type SYMBOL))
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

(defrule PINKYrunsAwayMSPACMANclosePPill
	(MSPACMAN (nearToPowerPill true))
	=>  
	(assert 
		(ACTION 
			(id PINKYrunsAway)
			(info "MSPacMan cerca PPill")
			(priority 50)
			(runawaystrategy POWERPILL)
		)
	)
)

(defrule PINKYrunsAwayLastPPill
	(GAME (onlyOnePowerPillLeft true))
	(PINKY (edible true))
	=>  
	(assert 
		(ACTION (id PINKYrunsAway) (info "Alejarse de la ultima PP") (priority 40) 
			(runawaystrategy LASTPOWERPILL)
		)
	)
)

(defrule PINKYchasesNotEdibleGhost
	(PINKY (edible true) (nearToNotEdibleGhost true))
	=>  
	(assert 
		(ACTION 
			(id PINKYchases)
			(info "Comestible --> huir hacia fantasma no comestible")
			(priority 40)
			(chasestrategy GHOST)
		)
	)
)

(defrule PINKYrunsAwayPacman
	(PINKY (edible true) (nearToPacman true))
	=>  
	(assert 
		(ACTION 
			(id PINKYrunsAway)
			(info "Comestible --> huir")
			(priority 30)
			(runawaystrategy PACMAN)
		)
	)
)

(defrule PINKYspread
  (PINKY (edible true) (nearToEdibleGhost true))
  =>
  (assert (ACTION (id PINKYrunsAway)
                  (info "Comestible --> dispersarse de otro fantasma comestible")
                  (priority 25)
                  (runawaystrategy SCATTER)))
)

(defrule PINKYcircleAroundLastPowerPill
    (GAME (onlyOnePowerPillLeft true))
    (PINKY (edible false)(ghostInPowerPill true))
    => 
    (assert 
    	(ACTION (id PINKYchases) (info "Solo 1 queda una PP --> girar alrededor PowerPill")  (priority 15) 
    		(chasestrategy CIRCLE_POWERPILL)
    	)
    )
)

(defrule PINKYchasesLastPowerPill
    (GAME (onlyOnePowerPillLeft true))
    (PINKY (edible false) (ghostInPowerPill false))
    => 
    (assert 
    	(ACTION (id PINKYchases) (info "Solo 1 queda una PP --> perseguir PowerPill")  (priority 15) 
    		(chasestrategy POWERPILL)
    	)
    )
)


(defrule PINKYchasesPacman
    (PINKY (edible false)
             (distToPacman ?dp)
             (distToPacmanJunction ?dj)
             (distToPacmanPowerPill ?dpill))
    (test (< ?dp ?dj))
    (test (< ?dp ?dpill))
    =>
    (assert (ACTION (id PINKYchases)
                    (info "Pacman es el más cercano")
                    (priority 20)
                    (chasestrategy PACMAN)))
)

(defrule PINKYchasesJunction
    (PINKY (edible false)
             (distToPacman ?dp)
             (distToPacmanJunction ?dj)
             (distToPacmanPowerPill ?dpill))
    (test (< ?dj ?dp))
    (test (< ?dj ?dpill))
    =>
    (assert (ACTION (id PINKYchases)
                    (info "Junction más cercana")
                    (priority 20)
                    (chasestrategy JUNCTION)))
)

(defrule PINKYchasesPill
    (PINKY (edible false)
             (distToPacman ?dp)
             (distToPacmanJunction ?dj)
             (distToPacmanPowerPill ?dpill))
    (test (< ?dpill ?dp))
    (test (< ?dpill ?dj))
    =>
    (assert (ACTION (id PINKYchases)
                    (info "Pill más cercana")
                    (priority 20)
                    (chasestrategy PILL)))
)
