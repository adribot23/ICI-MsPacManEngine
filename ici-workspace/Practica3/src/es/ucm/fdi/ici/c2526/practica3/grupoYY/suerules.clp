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
	(slot id)
	(slot info (default ""))
	(slot priority (type NUMBER)) ; mandatory slots
	(slot runawaystrategy (type SYMBOL)) ; Extra slot for the runaway action
	(slot chasestrategy (type SYMBOL)) ; Extra slot for the chase action
) 

;RULES 

(defrule SUErunsAwayMSPACMANclosePPill
	(MSPACMAN (nearToPowerPill true))
	(SUE (edible false))
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

(defrule SUErunsAwayLastPPill
	(GAME (onlyOnePowerPillLeft true))
	(SUE (edible true))
	=>  
	(assert 
		(ACTION (id SUErunsAway) (info "Alejarse de la ultima PP") (priority 50) 
			(runawaystrategy LASTPOWERPILL)
		)
	)
)

(defrule SUEspread
  (SUE (edible true) (nearToEdibleGhost true))
  =>
  (assert (ACTION (id SUErunsAway)
                  (info "Comestible --> dispersarse de otro fantasma comestible")
                  (priority 45)
                  (runawaystrategy SCATTER)))
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
			(priority 35)
			(runawaystrategy PACMAN)
		)
	)
)

(defrule SUEchasesLastPowerPill
    (GAME (onlyOnePowerPillLeft true))
    (SUE (edible false))
    => 
    (assert 
    	(ACTION (id SUEchases) (info "Solo 1 queda una PP --> perseguir PowerPill")  (priority 15) 
    		(chasestrategy POWERPILL)
    	)
    )
)


(defrule SUEchasesPacman
    (SUE (edible false)
             (distToPacman ?dp)
             (distToPacmanJunction ?dj)
             (distToPacmanPill ?dpill))
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
             (distToPacmanPill ?dpill))
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
             (distToPacmanPill ?dpill))
    (test (< ?dpill ?dp))
    (test (< ?dpill ?dj))
    =>
    (assert (ACTION (id SUEchases)
                    (info "Pill más cercana")
                    (priority 20)
                    (chasestrategy PILL)))
)