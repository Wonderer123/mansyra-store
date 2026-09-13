window.addEventListener("pageshow", function(event) {

if (event.persisted) {
    window.location.reload();
}

});
window.addEventListener("popstate", function() {
location.reload();
});
/* ================= USER ================= */

let currentUser = localStorage.getItem("user");
let cartItems = [];

if (currentUser) {
cartItems = JSON.parse(localStorage.getItem("cart_"+currentUser)) || [];
}

let recentlyAddedCount = 0;
let popupTimeout = null;


/* ================= PAGE LOAD ================= */

document.addEventListener("DOMContentLoaded",function(){

/* RELOAD USER + CART AFTER LOGIN */

currentUser = localStorage.getItem("user");

if(currentUser){
cartItems = JSON.parse(localStorage.getItem("cart_"+currentUser)) || [];
}else{
cartItems = [];
}

updateCartBadge();

/* ADD TO CART BUTTONS */

/* ADD TO CART EVENT DELEGATION */

document.addEventListener("click", function(e){

const btn = e.target.closest(".add-to-cart");

if(btn){

e.preventDefault(); // VERY IMPORTANT
addToCart(btn);

}

});


/* CART ICON */

const cartIcon=document.getElementById("cart-icon");

if(cartIcon){
cartIcon.onclick=()=>{

if(!localStorage.getItem("user")){
showLoginPopup();
return;
}

window.location.href="cart.html";

};
}


/* CART PAGE */

const container=document.getElementById("cart-container");

if(container){

currentUser = localStorage.getItem("user");

cartItems = JSON.parse(localStorage.getItem("cart_"+currentUser)) || [];

renderCart(container);

}


/* CLEAR CART */

const clearBtn=document.getElementById("clear-cart-btn");

if(clearBtn){
clearBtn.onclick=clearCart;
}


/* ORDER */

const orderBtn=document.getElementById("order-now-btn");

if(orderBtn){
orderBtn.onclick=()=>{
window.location.href="/checkout.html";
};
}

});


/* ================= ADD TO CART ================= */

function addToCart(button){
console.log("ADD TO CART CLICKED");
currentUser = localStorage.getItem("user");

if(!currentUser){
showLoginPopup();
return;
}
cartItems = JSON.parse(localStorage.getItem("cart_"+currentUser)) || [];
if(!Array.isArray(cartItems)) cartItems = [];

/* PRODUCT CARD DETECTION */

const card =
button.closest(".product-card") ||
button.closest(".card") ||
button.parentElement;

if(!card) return;


/* IMAGE */

const img=card.querySelector("img");


/* PRICE */

const priceText =
card.querySelector(".price")?.innerText ||
card.querySelector("h4")?.innerText ||
"0";

const price=parseInt(priceText.replace(/[^\d]/g,""));


/* NAME */

const name=card.querySelector("h3")?.innerText || "Item";


/* ITEM */

const item={
name:name,
price:price,
image:img?.src || "",
category: button.dataset.category
};


/* SAVE LOCAL */

cartItems.push(item);

localStorage.setItem("cart_"+currentUser,JSON.stringify(cartItems));


/* SERVER SYNC */

fetch("/api/cart",{
method:"POST",
headers:{ "Content-Type":"application/json"},
body:JSON.stringify({user:currentUser,item:item})
}).catch(()=>{});


/* UI */

recentlyAddedCount++;

animateFly(img);

updateCartBadge();

showPopup();

}


/* ================= BADGE ================= */

function updateCartBadge(){

const badge = document.getElementById("cart-badge");
if(!badge) return;

const user = localStorage.getItem("user");

if(!user){
badge.innerText="0";
return;
}

let cart = localStorage.getItem("cart_"+user);

if(!cart){
badge.innerText="0";
return;
}

cart = JSON.parse(cart);

badge.innerText = Array.isArray(cart) ? cart.length : 0;

}


/* ================= TOTALS ================= */

function calculateTotals(){

let subtotal=0;

cartItems.forEach(item=>{
subtotal+=item.price;
});

let tax=Math.round(subtotal*0.18);

let delivery=cartItems.length?100:0;

let total=subtotal+tax+delivery;

return{
subtotal,
tax,
delivery,
total
};

}


/* ================= RENDER CART ================= */

function renderCart(container){

container.innerHTML="";

const summary=document.getElementById("cart-summary");


/* EMPTY CART */

if(cartItems.length===0){

if(summary) summary.style.display="none";

container.innerHTML=`

<div class="empty-cart">
<div class="empty-icon">🛒</div>

<h2>Your Cart is Empty</h2>

<a href="/goof.html#shop" class="add-items-btn">
Add Items
</a>

</div>

`;

updateCartBadge();
return;

}


/* ITEMS */

cartItems.forEach((item,index)=>{

const div=document.createElement("div");

div.className="cart-item";

div.innerHTML=`

<img src="${item.image}">

<div class="cart-info">

<h3>${item.name}</h3>

<p>₹${item.price}</p>

<button onclick="removeItem(${index})">Remove</button>

</div>

`;

container.appendChild(div);

});


/* TOTALS */

const totals=calculateTotals();

if(summary){

summary.style.display="block";

document.getElementById("subtotal").innerText=totals.subtotal;
document.getElementById("tax").innerText=totals.tax;
document.getElementById("delivery").innerText=totals.delivery;
document.getElementById("total").innerText=totals.total;

}

updateCartBadge();

}


/* ================= REMOVE ITEM ================= */

function removeItem(index){

const removed=cartItems[index];

cartItems.splice(index,1);

localStorage.setItem("cart_"+currentUser,JSON.stringify(cartItems));

fetch("/api/cart/remove",{
method:"POST",
headers:{ "Content-Type":"application/json"},
body:JSON.stringify({user:currentUser,item:removed})
}).catch(()=>{});

const container=document.getElementById("cart-container");

if(container) renderCart(container);

updateCartBadge();

}


/* ================= CLEAR CART ================= */

function clearCart(){

cartItems=[];

localStorage.removeItem("cart_"+currentUser);

fetch("/api/cart/clear",{
method:"POST",
headers:{ "Content-Type":"application/json"},
body:JSON.stringify({user:currentUser})
}).catch(()=>{});

const container=document.getElementById("cart-container");

if(container) renderCart(container);

updateCartBadge();

}


/* ================= POPUP ================= */

function showPopup(){

let popup=document.getElementById("flash-popup");

if(!popup){

popup=document.createElement("div");
popup.id="flash-popup";
popup.className="flash-popup";

document.body.appendChild(popup);

}

popup.style.display="block";

popup.innerHTML=`${recentlyAddedCount} item${recentlyAddedCount>1?"s":""} added to cart`;

clearTimeout(popupTimeout);

popupTimeout=setTimeout(()=>{
popup.style.display="none";
recentlyAddedCount=0;
},2000);

}


/* ================= LOGIN POPUP ================= */

function showLoginPopup(){

let popup=document.getElementById("login-popup");

if(!popup){

popup=document.createElement("div");
popup.id="login-popup";
popup.className="flash-popup";

document.body.appendChild(popup);

}

popup.style.display="block";

popup.innerHTML=`

Please login to use the cart

<br><br>

<button onclick="document.getElementById('login-popup').style.display='none'">
OK
</button>

<button onclick="window.location.href='/login.html'">
Login
</button>

`;

}


/* ================= FLY ANIMATION ================= */

function animateFly(image){

if(!image) return;

const cartIcon=document.getElementById("cart-icon");
if(!cartIcon) return;

const clone=image.cloneNode(true);

clone.style.position="fixed";
clone.style.zIndex="9999";
clone.style.width="120px";
clone.style.transition="all 0.7s ease-in-out";

document.body.appendChild(clone);

const rect=image.getBoundingClientRect();

clone.style.left=rect.left+"px";
clone.style.top=rect.top+"px";

const cart=cartIcon.getBoundingClientRect();

setTimeout(()=>{

clone.style.left=cart.left+"px";
clone.style.top=cart.top+"px";
clone.style.width="20px";
clone.style.opacity="0.3";
clone.style.transform="scale(0.2)";

},50);

setTimeout(()=>{

clone.remove();
cartIcon.classList.add("cart-shake");

setTimeout(()=>{
cartIcon.classList.remove("cart-shake");
},1000);

},700);

}