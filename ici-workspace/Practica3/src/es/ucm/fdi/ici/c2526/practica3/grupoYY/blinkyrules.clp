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
	(slot id) (slot info (default "")) (slot priority (type NUMBER) ) ; mandatory slots
	(slot runawaystrategy (type SYMBOL)) ; Extra slot for the runaway action
	(slot chasestrategy (type SYMBOL)) ; Extra slot for the chase action
) 

;RULES 

(defrule BLINKYrunsAwayMSPACMANclosePPill
	(MSPACMAN (nearToPowerPill true))
	=>  
	(assert 
		(ACTION (id BLINKYrunsAway) (info "MSPacMan cerca PPill") (priority 50) 
			(runawaystrategy POWERPILL)
		)
	)
)


(defrule BLINKYchasesNotEdibleGhost
	(BLINKY (edible true) (nearToNotEdibleGhost true)) 
	=>  
	(assert 
		(ACTION (id BLINKYchases) (info "Comestible --> huir hacia fantasma no comestible") (priority 40) 
			(chasestrategy GHOST)
		)
	)
)
	
	
(defrule BLINKYrunsAwayPacman
	(BLINKY (edible true) (nearToPacman true)) 
	=>  
	(assert 
		(ACTION (id BLINKYrunsAway) (info "Comestible --> huir") (priority 30) 
			(runawaystrategy PACMAN)
		)
	)
)

(defrule BLINKYspread
  (BLINKY (edible true) (nearToEdibleGhost true))
  =>
  (assert (ACTION (id BLINKYrunsAway)
                  (info "Comestible --> dispersarse de otro fantasma comestible")
                  (priority 25)
                  (runawaystrategy SCATTER)))
)

(defrule BLINKYcircleAroundLastPowerPill
    (GAME (onlyOnePowerPillLeft true))
    (BLINKY (edible false)(ghostInPowerPill true))
    => 
    (assert 
    	(ACTION (id BLINKYchases) (info "Solo 1 queda una PP --> girar alrededor PowerPill")  (priority 25) 
    		(chasestrategy CIRCLE_POWERPILL)
    	)
    )
)

(defrule BLINKYchasesLastPowerPill
    (GAME (onlyOnePowerPillLeft true))
    (BLINKY (edible false)(ghostInPowerPill false))
    => 
    (assert 
    	(ACTION (id BLINKYchases) (info "Solo 1 queda una PP --> perseguir PowerPill")  (priority 25) 
    		(chasestrategy POWERPILL)
    	)
    )
)

(defrule BLINKYchasesPacman
    (BLINKY (edible false)
             (distToPacman ?dp)
             (distToPacmanJunction ?dj)
             (distToPacmanPowerPill ?dpill))
    (test (< ?dp ?dj))
    (test (< ?dp ?dpill))
    =>
    (assert (ACTION (id BLINKYchases)
                    (info "Pacman es el más cercano")
                    (priority 20)
                    (chasestrategy PACMAN)))
)

(defrule BLINKYchasesJunction
    (BLINKY (edible false)
             (distToPacman ?dp)
             (distToPacmanJunction ?dj)
             (distToPacmanPowerPill ?dpill))
    (test (< ?dj ?dp))
    (test (< ?dj ?dpill))
    =>
    (assert (ACTION (id BLINKYchases)
                    (info "Junction más cercana")
                    (priority 20)
                    (chasestrategy JUNCTION)))
)

(defrule BLINKYchasesPill
    (BLINKY (edible false)
             (distToPacman ?dp)
             (distToPacmanJunction ?dj)
             (distToPacmanPowerPill ?dpill))
    (test (< ?dpill ?dp))
    (test (< ?dpill ?dj))
    =>
    (assert (ACTION (id BLINKYchases)
    				(info "Pill más cercana")
    				(priority 20)
    				(chasestrategy PILL)))
)

