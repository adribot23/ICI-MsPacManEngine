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


(deftemplate MSPACMAN 
    (slot nearToPowerPill (type SYMBOL))
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

(defrule PINKYchasesLastPowerPill
    (GAME (onlyOnePowerPillLeft true) (firstGhost ?f) (secondGhost ?s))
    (PINKY (edible false))
    (test (or (= ?f 1) (= ?s 1)))
    => 
    (assert 
    	(ACTION (id PINKYchases) (info "Solo 1 queda una PP --> perseguir PowerPill")  (priority 55) 
    		(chasestrategy POWERPILL)
    	)
    )
)


(defrule PINKYspread
  (PINKY (edible true) (nearToEdibleGhost true) (nearToPacman false))
  =>
  (assert (ACTION (id PINKYrunsAway)
                  (info "Comestible --> dispersarse de otro fantasma comestible")
                  (priority 55)
                  (runawaystrategy SCATTER)))
)

(defrule PINKYrunsAwayLastPPill
	(GAME (onlyOnePowerPillLeft true))
	(PINKY (edible true))
	=>  
	(assert 
		(ACTION (id PINKYrunsAway) (info "Alejarse de la ultima PP") (priority 50) 
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
			(priority 35)
			(runawaystrategy PACMAN)
		)
	)
)

(defrule PINKYalone
 	(PINKY (edible true))
	=>
	(assert 
		(ACTION (id PINKYrunsAway) (info "Comestible y solo --> acercarse a power pill") (priority 30) 
			(runawaystrategy ALONE)
		)
	)
)

(defrule PINKYchasesPacman
    (PINKY (edible false))
    (GAME (nearestGhostToPacman ?g))
    (test (= ?g 1))
    =>
    (assert (ACTION (id PINKYchases)
                    (info "Pacman es el más cercano")
                    (priority 20)
                    (chasestrategy PACMAN)))
)

(defrule PINKYchasesFirstJunction
    (PINKY (edible false))
    (GAME (nearestGhostToFirstJunction ?g))
    (test (= ?g 1))
    =>
    (assert (ACTION (id PINKYchases)
                    (info "First Junction más cercana")
                    (priority 20)
                    (chasestrategy FIRSTJUNCTION)))
)

(defrule PINKYchasesSecondJunction
    (PINKY (edible false))
    (GAME (nearestGhostToSecondJunction ?g))
    (test (= ?g 1))
    =>
    (assert (ACTION (id PINKYchases)
                    (info "Second Junction más cercana")
                    (priority 20)
                    (chasestrategy SECONDJUNCTION)))
)

(defrule PINKYchasesThirdJunction
    (PINKY (edible false))
    (GAME (nearestGhostToThirdJunction ?g))
    (test (= ?g 1))
    =>
    (assert (ACTION (id PINKYchases)
                    (info "Third Junction más cercana")
                    (priority 20)
                    (chasestrategy THIRDJUNCTION)))
)

(defrule PINKYnoThirdJunction
    (PINKY (edible false))
    (GAME (nearestGhostToFirstJunction ?f) (nearestGhostToSecondJunction ?s) (nearestGhostToThirdJunction ?t))
    (test (or (= ?f -1) (= ?s -1) (= ?t -1)))
    =>
    (assert (ACTION (id PINKYchases)
                     (info "Sin Junction --> perseguir objetivo más cercano")
                    (priority 19)
                    (chasestrategy NEARESTTARGET)))
)