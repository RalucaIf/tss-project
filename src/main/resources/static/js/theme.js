const chatBox = document.getElementById('chat-box');
const openChatButton = document.getElementById('open-chat');
const closeChatButton = document.getElementById('close-chat');
const chatInput = document.getElementById('chat-input');
const chatMessages = document.getElementById('chat-messages');

// Deschidere chat
openChatButton.addEventListener('click', () => {
    chatBox.style.display = 'flex';
});

// Închidere chat
closeChatButton.addEventListener('click', () => {
    chatBox.style.display = 'none';
});

// Adaugă mesaj în chat
function addMessage(sender, text) {
    const msgDiv = document.createElement('div');
    msgDiv.textContent = text;
    msgDiv.classList.add(sender === 'Tu' ? 'user-msg' : 'expert-msg');
    chatMessages.appendChild(msgDiv);
    chatMessages.scrollTop = chatMessages.scrollHeight;
}

// Trimitere mesaj la Enter
chatInput.addEventListener('keypress', async function(e){
    if(e.key === 'Enter' && chatInput.value.trim() !== '') {
        const message = chatInput.value.trim();
        addMessage('Tu', message);
        chatInput.value = '';

        // Mesaj temporar expert
        const tempDiv = document.createElement('div');
        tempDiv.textContent = '...';
        tempDiv.classList.add('expert-msg');
        chatMessages.appendChild(tempDiv);
        chatMessages.scrollTop = chatMessages.scrollHeight;

        try {
            const response = await fetch('http://localhost:8080/api/chat', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ message })
            });

            const data = await response.json();
            tempDiv.textContent = data.reply;
        } catch (err) {
            tempDiv.textContent = 'Îmi pare rău, a apărut o eroare.';
            console.error(err);
        }
    }
});
