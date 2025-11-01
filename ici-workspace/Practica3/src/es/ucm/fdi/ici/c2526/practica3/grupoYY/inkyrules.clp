;FACTS ASSERTED BY GAME INPUT
(deftemplate BLINKY
	(slot edible (type SYMBOL))
	(slot nearToNotEdibleGhost (type SYMBOL))
	(slot nearToEdibleGhost (type SYMBOL))
	(slot nearToPacman (type SYMBOL))
)	
(deftemplate INKY
	(slot edible (type SYMBOL))
	(slot nearToNotEdibleGhost (type SYMBOL))
	(slot nearToEdibleGhost (type SYMBOL))
	(slot nearToPacman (type SYMBOL))
)	
	
(deftemplate PINKY
	(slot edible (type SYMBOL))
	(slot nearToNotEdibleGhost (type SYMBOL))
	(slot nearToEdibleGhost (type SYMBOL))
	(slot nearToPacman (type SYMBOL))
)

(deftemplate SUE
	(slot edible (type SYMBOL))
	(slot nearToNotEdibleGhost (type SYMBOL))
	(slot nearToEdibleGhost (type SYMBOL))
	(slot nearToPacman (type SYMBOL))
)
    
(deftemplate GAME
    (slot onlyOnePowerPillLeft (type SYMBOL))
    (slot lastPills (type SYMBOL))
    (slot firstGhost (type INTEGER) (default -1))
    (slot secondGhost (type INTEGER) (default -1))
    (slot nearestGhostToPacman (type INTEGER) (default -1))
    (slot nearestGhostToFirstJunction (type INTEGER) (default -1))
    (slot nearestGhostToSecondJunction (type INTEGER) (default -1))
    (slot nearestGhostToThirdJunction (type INTEGER) (default -1))
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

(defrule INKYchasesLastPowerPill
    (GAME (onlyOnePowerPillLeft true) (firstGhost ?f) (secondGhost ?s))
    (INKY (edible false))
    (test (or (= ?f 2) (= ?s 2)))
    => 
    (assert 
    	(ACTION (id INKYchases) (info "Solo 1 queda una PP --> perseguir PowerPill")  (priority 55) 
    		(chasestrategy POWERPILL)
    	)
    )
)

(defrule INKYspread
  (INKY (edible true) (nearToEdibleGhost true) (nearToPacman false))
  =>
  (assert (ACTION (id INKYrunsAway)
                  (info "Comestible --> dispersarse de otro fantasma comestible")
                  (priority 55)
                  (runawaystrategy SCATTER)))
)

(defrule INKYrunsAwayLastPPill
	(GAME (onlyOnePowerPillLeft true))
	(INKY (edible true))
	=>  
	(assert 
		(ACTION (id INKYrunsAway) (info "Alejarse de la ultima PP") (priority 50) 
			(runawaystrategy LASTPOWERPILL)
		)
	)
)

(defrule INKYchasesNotEdibleGhost
	(INKY (edible true) (nearToNotEdibleGhost true))
	=>  
	(assert 
		(ACTION 
			(id INKYchases)
			(info "Comestible --> huir hacia fantasma no comestible")
			(priority 40)
			(chasestrategy GHOST)
		)
	)
)

(defrule INKYrunsAwayPacman
	(INKY (edible true) (nearToPacman true))
	=>  
	(assert 
		(ACTION 
			(id INKYrunsAway)
			(info "Comestible --> huir")
			(priority 35)
			(runawaystrategy PACMAN)
		)
	)
)

(defrule INKYalone
 	(INKY (edible true))
	=>
	(assert 
		(ACTION (id INKYrunsAway) (info "Comestible y solo --> ir al nodo mas lejano de pacman") (priority 30) 
			(runawaystrategy ALONE)
		)
	)
)

(defrule INKYchasesPacman
    (INKY (edible false))
    (GAME (nearestGhostToPacman ?g))
    (test (= ?g 2))
    =>
    (assert (ACTION (id INKYchases)
                    (info "Pacman es el más cercano")
                    (priority 20)
                    (chasestrategy PACMAN)))
)

(defrule INKYchasesFirstJunction
    (INKY (edible false))
    (GAME (nearestGhostToFirstJunction ?g))
    (test (= ?g 2))
    =>
    (assert (ACTION (id INKYchases)
                    (info "First Junction más cercana")
                    (priority 20)
                    (chasestrategy FIRSTJUNCTION)))
)

(defrule INKYchasesSecondJunction
    (INKY (edible false))
    (GAME (nearestGhostToSecondJunction ?g))
    (test (= ?g 2))
    =>
    (assert (ACTION (id INKYchases)
                    (info "Second Junction más cercana")
                    (priority 20)
                    (chasestrategy SECONDJUNCTION)))
)

(defrule INKYchasesThirdJunction
    (INKY (edible false))
    (GAME (nearestGhostToThirdJunction ?g))
    (test (= ?g 2))
    =>
    (assert (ACTION (id INKYchases)
                    (info "Third Junction más cercana")
                    (priority 20)
                    (chasestrategy THIRDJUNCTION)))
)

(defrule INKYnoThirdJunction
    (INKY (edible false))
    (GAME (nearestGhostToFirstJunction ?f) (nearestGhostToSecondJunction ?s) (nearestGhostToThirdJunction ?t))
    (test (or (= ?f -1) (= ?s -1) (= ?t -1)))
    =>
    (assert (ACTION (id INKYchases)
                    (info "Sin Junction --> perseguir objetivo más cercano")
                    (priority 19)
                    (chasestrategy NEARESTTARGET)))
)
