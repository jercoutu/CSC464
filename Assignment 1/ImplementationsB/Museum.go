package main

import (
	"log"
	"sync"
	"time"
)

const (
	maxWaiting = 7
	nCustomers = 10
	
)

var (
	lobby = make(chan chan int, maxWaiting)
	wg = new(sync.WaitGroup)
)

func museum() {
	for ch, ok := <-lobby; ok ; ch, ok = <-lobby {
		log.Println("Customer", <-ch, "enters the Museum.")
		time.Sleep(500)
		log.Println("Customer exists the museum")
		ch <- 0
	}
}

func customer(id int) {
	defer wg.Done()
	ch := make(chan int)
	log.Println("Customer", id, "Wants to enter the Museum.")
	select {
	case lobby <- ch:		
		ch <- id
		<- ch
	default:
		log.Println("Customer", id, "decides today isn't a good day for a Museum.")
	}
}

func main() {
	wg.Add(nCustomers)
	go museum()
	log.Println("Museum Open")
	for i := 0; i < nCustomers; i++ {
		go customer(i)
	}
	wg.Wait()
	log.Println("Museum closed")
}
