const chatBox = document.getElementById('chat-box');
const openChatButton = document.getElementById('open-chat');
const closeChatButton = document.getElementById('close-chat');

// Deschidere cu fade
openChatButton.addEventListener('click', () => {
    chatBox.classList.add('show');
});

// Închidere cu fade
closeChatButton.addEventListener('click', () => {
    chatBox.classList.remove('show');
});

// Funcția pentru schimbarea temei chat-ului
function updateChatTheme(theme) {
    if(theme === 'dark-background') {
        chatBox.classList.add('chat-dark');
        chatBox.classList.remove('chat-light');
    } else {
        chatBox.classList.add('chat-light');
        chatBox.classList.remove('chat-dark');
    }
}
