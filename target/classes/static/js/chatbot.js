document.addEventListener("DOMContentLoaded", () => {
    const chatbotToggler = document.querySelector(".chatbot-toggler");
    const chatbox = document.querySelector(".chatbox");
    const chatInput = document.querySelector(".chat-input textarea");
    const sendChatBtn = document.querySelector(".chat-input button");

    let userMessage;
    const inputInitHeight = chatInput.scrollHeight;

    const createChatLi = (message, className) => {
        const chatLi = document.createElement("li");
        chatLi.classList.add("chat", className);
        chatLi.innerHTML = `<p>${message}</p>`;
        return chatLi;
    };

    const getResponse = (userMsg) => {
        userMsg = userMsg.toLowerCase();
        
        if (userMsg.includes("hello") || userMsg.includes("hi")) {
            return "Hi there! I'm LoopBot. How can I help you navigate CampusLoop today?";
        } else if (userMsg.includes("sell") || userMsg.includes("post")) {
            return "To sell an item, click on the 'Post Item' button in the navigation bar. Make sure you're logged in!";
        } else if (userMsg.includes("buy")) {
            return "Browse the marketplace on the home page. Click on any item to see details and contact the seller via messaging.";
        } else if (userMsg.includes("price") || userMsg.includes("negotiate")) {
            return "Negotiation is common! You can message the seller directly to discuss a fair price.";
        } else if (userMsg.includes("safe") || userMsg.includes("trust")) {
            return "CampusLoop is only for verified students. Always meet in public campus locations (like the Library or Canteen) during daylight for exchanges.";
        } else if (userMsg.includes("category") || userMsg.includes("what can i sell")) {
            return "You can sell Books, Electronics, Cycles, Hostel Items, and Lab Equipment!";
        } else {
            return "I'm still learning! You can ask me about selling items, safety tips, or marketplace categories.";
        }
    };

    const handleChat = () => {
        userMessage = chatInput.value.trim();
        if (!userMessage) return;
        chatInput.value = "";
        chatInput.style.height = `${inputInitHeight}px`;

        chatbox.appendChild(createChatLi(userMessage, "outgoing"));
        chatbox.scrollTo(0, chatbox.scrollHeight);

        setTimeout(() => {
            const incomingChatLi = createChatLi("Thinking...", "incoming");
            chatbox.appendChild(incomingChatLi);
            chatbox.scrollTo(0, chatbox.scrollHeight);

            setTimeout(() => {
                incomingChatLi.querySelector("p").textContent = getResponse(userMessage);
                chatbox.scrollTo(0, chatbox.scrollHeight);
            }, 600);
        }, 300);
    };

    chatbotToggler.addEventListener("click", () => document.body.classList.toggle("show-chatbot"));
    sendChatBtn.addEventListener("click", handleChat);
    chatInput.addEventListener("keydown", (e) => {
        if (e.key === "Enter" && !e.shiftKey) {
            e.preventDefault();
            handleChat();
        }
    });
});
