import java.util.*;
import java.util.AbstractMap;

// Simplified Expense Sharing Application
// Supports: Groups, Equal/Exact/Percentage splits, Balance tracking and settle dues

class ExpenseSharingApplication{

private Map<String, Set<String>> groups = new HashMap<>();
private Map<String, Map<String, Double>> balance = new HashMap<>();

// creating groups
public void createGroups(String groupId, List<String> members) {
groups.put(groupId, new HashSet<>(members));
for(String m:members){
   balance.putIfAbsent(m,new HashMap<>());
  }
}

    

// simpliying the balance
private void simplifyBalances(){
Map<String, Double> netBalance = new HashMap<>();
for(String u:balance.keySet()){
  for(String v:balance.get(u).keySet()){
     double amt = balance.get(u).get(v);
     netBalance.put(u,netBalance.getOrDefault(u,0.0) - amt);
     netBalance.put(v,netBalance.getOrDefault(v,0.0) + amt);
     }
 }

 List<Map.Entry<String,Double>> debtors = new ArrayList<>();
 List<Map.Entry<String,Double>> creditors = new ArrayList<>();

 for(Map.Entry<String,Double> e:netBalance.entrySet()){
     if(e.getValue()<0){
           debtors.add(new AbstractMap.SimpleEntry<>(e.getKey(),-e.getValue()));
        } else if(e.getValue()>0){
                creditors.add(new AbstractMap.SimpleEntry<>(e.getKey(),e.getValue()));
          }
     }

balance.clear();
int i = 0,j = 0;

while(i<debtors.size() && j<creditors.size()){
 String debtor=debtors.get(i).getKey();
 double dAmt=debtors.get(i).getValue();
 String creditor=creditors.get(j).getKey();
 double cAmt=creditors.get(j).getValue();
 double settle=Math.min(dAmt,cAmt);
 balance.putIfAbsent(debtor,new HashMap<>());
 balance.get(debtor).put(creditor,settle);

 debtors.set(i,new AbstractMap.SimpleEntry<>(debtor,dAmt - settle));
 creditors.set(j,new AbstractMap.SimpleEntry<>(creditor,cAmt - settle));
 if(debtors.get(i).getValue()==0) i++;
 if(creditors.get(j).getValue()==0) j++;
 } 
}

// handing expenses by split types
public void addExpense(String groupId,String paidBy,double amount,String splitType,Map<String,Double> splitDetails){
Set<String> members=groups.get(groupId);
Map<String,Double> shares=new HashMap<>();

if(splitType.equals("EQUAL")){
 double share=amount/members.size();
 for(String m:members){
     shares.put(m,share);
 }
}
else if(splitType.equals("EXACT")){
            shares.putAll(splitDetails);
}
else if(splitType.equals("PERCENT")){
 for(String user:splitDetails.keySet()){
    double percent=splitDetails.get(user);
    shares.put(user,amount*percent/100);
    } 
}
else{
   throw new IllegalArgumentException("Invalid split type");
}

 //  balance update
 for(String user:shares.keySet()){
       if(!user.equals(paidBy)){
            balance.putIfAbsent(user,new HashMap<>());
            balance.get(user).put(
                        paidBy,
                        balance.get(user).getOrDefault(paidBy, 0.0)+shares.get(user)
                );
            }
    }

        simplifyBalances();
   }

// settle dues
public void settle(String payer,String receiver){
        if (balance.containsKey(payer)) {
            balance.get(payer).remove(receiver);
        }
 }
// How much they owes others
public void showUserOwes(String user){
System.out.println(user+" owes:");
if(!balance.containsKey(user)||balance.get(user).isEmpty()){
     System.out.println(" Nothing");
     return;
    }
for(String u :balance.get(user).keySet()){
   System.out.println("  "+u+" : Rs."+balance.get(user).get(u));
   }
}

// How much others owe them
public void showOthersOweUser(String user){
System.out.println("Others owe "+user+":");
boolean found=false;
for(String u:balance.keySet()){
   if(balance.get(u).containsKey(user)){
                System.out.println("  "+u+" : Rs."+balance.get(u).get(user));
                found = true;
            }
    }
if(!found){
            System.out.println(" Nothing");
        }  
 }

// balance display
public void showBalances(){
System.out.println("Current Balances:");
  for(String u:balance.keySet()){
            for(String v:balance.get(u).keySet()){
                System.out.println(u +" owes "+v+" : Rs."+balance.get(u).get(v));
            }
       }
   }
}

// main class 
public class Expenses{
public static void main(String[] args){
ExpenseSharingApplication app = new ExpenseSharingApplication();
app.createGroups("trip",Arrays.asList("A","B","C"));
// Equal split
app.addExpense("trip","A",4000,"EQUAL",null);
// Exact split
Map<String, Double> exact=new HashMap<>();
exact.put("A",400.0);
exact.put("B",400.0);
exact.put("C",400.0);
app.addExpense("trip","B",1200,"EXACT",exact);
// Percentage split
Map<String, Double> percent=new HashMap<>();
percent.put("A",40.0);
percent.put("B",40.0);
percent.put("C",20.0);
app.addExpense("trip","C",1000,"PERCENT",percent);
// Show balance
app.showBalances();
System.out.println();
// User-wise views
app.showUserOwes("A");
app.showOthersOweUser("A");
System.out.println();
app.showUserOwes("B");
app.showOthersOweUser("B");
System.out.println();
app.showUserOwes("C");
app.showOthersOweUser("C");
}
}

