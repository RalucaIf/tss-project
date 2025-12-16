const chatBox = document.getElementById('chat-box');
const chatInput = document.getElementById('chat-input');
const chatMessages = document.getElementById('chat-messages');
const openChatButton = document.getElementById('contact-expert-btn'); // folosește butonul tău
const closeChatButton = document.getElementById('close-chat');

// Preluare nume user
const usernameEl = document.getElementById('chat-username');
const username = usernameEl ? usernameEl.textContent.trim() : 'User';

// Deschidere chat
openChatButton.addEventListener('click', () => {
    chatBox.style.display = 'flex'; // sau 'block', depinde de CSS

    // Adaugă mesaj de bun venit doar prima dată
    if (!chatBox.dataset.welcomeShown) {
        addMessage('Expert', `Bine ai venit, ${username}! Cu ce te pot ajuta?`);
        chatBox.dataset.welcomeShown = true;
    }
});

// Închidere chat
closeChatButton.addEventListener('click', () => {
    chatBox.style.display = 'none';
});

// Adaugă mesaj în chat
function addMessage(sender, text) {
    const msgDiv = document.createElement('div');
    msgDiv.textContent = text;

    if (sender === 'Tu') {
        msgDiv.classList.add('user-msg');
    } else {
        msgDiv.classList.add('expert-msg');
    }

    chatMessages.appendChild(msgDiv);
    chatMessages.scrollTop = chatMessages.scrollHeight;
}

// Trimite mesaj la Enter
chatInput.addEventListener('keypress', async function(e) {
    if (e.key === 'Enter' && chatInput.value.trim() !== '') {
        const message = chatInput.value.trim();
        addMessage('Tu', message);
        chatInput.value = '';

        const tempId = 'temp-' + Date.now();
        const tempDiv = document.createElement('div');
        tempDiv.id = tempId;
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
            if (data.bonusReceived === true) {
                showPointsAnimation(5);
                updateNavbarPoints(data.points);
            }
        } catch (err) {
            tempDiv.textContent = 'Îmi pare rău, a apărut o eroare.';
            console.error(err);
        }
    }
});
