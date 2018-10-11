package main

import (
	"fmt"
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
		fmt.Println("Customer", <-ch, "enters the Museum.")
		time.Sleep(500)
		fmt.Println("Customer exits the museum")
		ch <- 0
	}
}

func customer(id int) {
	defer wg.Done()
	ch := make(chan int)
	fmt.Println("Customer", id, "Wants to enter the Museum.")
	select {
	case lobby <- ch:		
		ch <- id
		<- ch
	default:
		fmt.Println("Customer", id, "decides today isn't a good day for a Museum.")
	}
}

func main() {
	wg.Add(nCustomers)
	go museum()
	fmt.Println("Museum Open")
	for i := 0; i < nCustomers; i++ {
		go customer(i)
	}
	wg.Wait()
	fmt.Println("Museum closed")
}
