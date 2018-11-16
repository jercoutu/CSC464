package vectorclock

import (
  "testing"
  "fmt"
)

func send(p1 *Process, p2 *Process) {
    p2.SendMsgFrom(p1)
}

func TestCase1(t *testing.T){
  alice := New("alice")
  ben   := New("ben")
  cathy := New("cathy")
  dave  := New("dave")

  // Alice says, “Let’s meet Wednesday,” to all the others
  alice.Set("Wednesday")
  send(alice, ben)
  send(alice, cathy)
  send(alice, dave)

  // Ben and dave start talking and ben suggests Tuesday and dave agrees, confirming Tuesday
  ben.Set("Tuesday")
  send(ben, dave)
  dave.Set("Tuesday")
  send(dave, ben)

  // Now cathy gets into the act suggesting thursday
  cathy.Set("Thursday")
  send(cathy, dave)

  // Dave has a conflict and makes a decision to go with thursday
  send(dave, cathy)

  //So now when Alice asks Ben and Cathy for the latest decision, the replies she receive are, from Ben
  send(ben, alice)
  // And from cathy
  send(cathy, alice)

  //Alice now show ben that he has been overruled
  send(alice, ben)

  fmt.Println("Dave's data \t", dave.Get(), dave.Vclock())
  fmt.Println("Cathy's data \t", cathy.Get(), cathy.Vclock())
  fmt.Println("Bens data \t", ben.Get(), ben.Vclock())
  fmt.Println("Alice data \t", alice.Get(), alice.Vclock())
}
