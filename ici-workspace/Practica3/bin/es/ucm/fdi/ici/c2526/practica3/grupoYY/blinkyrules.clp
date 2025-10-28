;FACTS ASSERTED BY GAME INPUT
(deftemplate BLINKY
	(slot edible (type SYMBOL))
	(slot nearToNotEdibleGhost (type SYMBOL))
	(slot nearToEdibleGhost (type SYMBOL))
	(slot nearToPacman (type SYMBOL))
	(slot distToPacman (type NUMBER))
    (slot distToPacmanJunction (type NUMBER))
    (slot distToPacmanPill (type NUMBER))
)	
(deftemplate INKY
	(slot edible (type SYMBOL))
	(slot nearToNotEdibleGhost (type SYMBOL))
	(slot nearToEdibleGhost (type SYMBOL))
	(slot nearToPacman (type SYMBOL))
	(slot distToPacman (type NUMBER))
    (slot distToPacmanJunction (type NUMBER))
    (slot distToPacmanPill (type NUMBER))
)	
	
(deftemplate PINKY
	(slot edible (type SYMBOL))
	(slot nearToNotEdibleGhost (type SYMBOL))
	(slot nearToEdibleGhost (type SYMBOL))
	(slot nearToPacman (type SYMBOL))
	(slot distToPacman (type NUMBER))
    (slot distToPacmanJunction (type NUMBER))
    (slot distToPacmanPill (type NUMBER))
)

(deftemplate SUE
	(slot edible (type SYMBOL))
	(slot nearToNotEdibleGhost (type SYMBOL))
	(slot nearToEdibleGhost (type SYMBOL))
	(slot nearToPacman (type SYMBOL))
	(slot distToPacman (type NUMBER))
    (slot distToPacmanJunction (type NUMBER))
    (slot distToPacmanPill (type NUMBER))
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

(defrule BLINKYchasesLastPowerPill
    (GAME (onlyOnePowerPillLeft true))
    (BLINKY (edible false))
    => 
    (assert 
    	(ACTION (id BLINKYchases) (info "Solo 1 queda una PP --> perseguir PowerPill")  (priority 55) 
    		(chasestrategy POWERPILL)
    	)
    )
)

(defrule BLINKYrunsAwayMSPACMANclosePPill
	(MSPACMAN (nearToPowerPill true))
	(BLINKY (edible false))
	=>  
	(assert 
		(ACTION (id BLINKYrunsAway) (info "MSPacMan cerca PPill") (priority 50) 
			(runawaystrategy POWERPILL)
		)
	)
)

(defrule BLINKYrunsAwayLastPPill
	(GAME (onlyOnePowerPillLeft true))
	(BLINKY (edible true))
	=>  
	(assert 
		(ACTION (id BLINKYrunsAway) (info "Alejarse de la ultima PP") (priority 50) 
			(runawaystrategy LASTPOWERPILL)
		)
	)
)

(defrule BLINKYspread
  (BLINKY (edible true) (nearToEdibleGhost true))
  =>
  (assert (ACTION (id BLINKYrunsAway)
                  (info "Comestible --> dispersarse de otro fantasma comestible")
                  (priority 45)
                  (runawaystrategy SCATTER)))
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
		(ACTION (id BLINKYrunsAway) (info "Comestible --> huir") (priority 35) 
			(runawaystrategy PACMAN)
		)
	)
)

(defrule BLINKYchasesPacman
    (BLINKY (edible false)
             (distToPacman ?dp)
             (distToPacmanJunction ?dj)
             (distToPacmanPill ?dpill))
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
             (distToPacmanPill ?dpill))
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
             (distToPacmanPill ?dpill))
    (test (< ?dpill ?dp))
    (test (< ?dpill ?dj))
    =>
    (assert (ACTION (id BLINKYchases)
    				(info "Pill más cercana")
    				(priority 20)
    				(chasestrategy PILL)))
)

