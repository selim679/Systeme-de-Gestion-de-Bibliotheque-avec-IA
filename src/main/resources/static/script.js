document.addEventListener('DOMContentLoaded', () => {
  const backendBaseUrl = 'http://localhost:8080/api';

  // --- DOM Elements ---
  const homeSection = document.getElementById('home-section');
  const booksSection = document.getElementById('books-section');
  const booksTableBody = document.getElementById('books-table-body');
  const viewBooksBtn = document.getElementById('view-books-btn');

  const chatbotContainer = document.getElementById('chatbot-container');
  const openChatbotBtn = document.getElementById('open-chatbot-btn');
  const closeChatbotBtn = document.getElementById('close-chatbot-btn');
  const chatWindow = document.getElementById('chat-window');
  const chatbotForm = document.getElementById('chatbot-form');
  const userMessageInput = document.getElementById('user-message-input');

  // --- Functions for UI Management ---
  function showSection(sectionToShow) {
    homeSection.classList.add('hidden');
    booksSection.classList.add('hidden');
    // Add other sections here if you expand the project

    sectionToShow.classList.remove('hidden');
  }

  function toggleChatbot() {
    chatbotContainer.classList.toggle('hidden');
    if (!chatbotContainer.classList.contains('hidden')) {
      chatWindow.scrollTop = chatWindow.scrollHeight; // Scroll to bottom on open
    }
  }

  function addMessageToChat(sender, text, books = []) {
    const messageDiv = document.createElement('div');
    messageDiv.classList.add('mb-3');

    const contentDiv = document.createElement('div');
    contentDiv.classList.add('p-3', 'rounded-lg', 'max-w-[75%]');

    if (sender === 'user') {
      messageDiv.classList.add('flex', 'justify-end');
      contentDiv.classList.add('bg-blue-500', 'text-white');
    } else {
      messageDiv.classList.add('flex', 'justify-start');
      contentDiv.classList.add('bg-gray-200', 'text-gray-800');
    }

    contentDiv.innerHTML = `<p>${text}</p>`;

    if (books.length > 0) {
      const booksList = document.createElement('div');
      booksList.classList.add('mt-2', 'border-t', 'border-gray-400', 'pt-2');
      booksList.innerHTML = '<p class="font-semibold">Suggestions :</p><ul class="list-disc list-inside">';
      books.forEach(book => {
        booksList.innerHTML += `<li>${book.titre} (${book.genre})</li>`;
      });
      booksList.innerHTML += '</ul>';
      contentDiv.appendChild(booksList);
    }

    messageDiv.appendChild(contentDiv);
    chatWindow.appendChild(messageDiv);
    chatWindow.scrollTop = chatWindow.scrollHeight; // Scroll to latest message
  }

  // --- API Calls ---
  async function fetchBooks() {
    try {
      const response = await fetch(`${backendBaseUrl}/books`);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const books = await response.json();
      displayBooks(books);
    } catch (error) {
      console.error('Error fetching books:', error);
      booksTableBody.innerHTML = `<tr><td colspan="4" class="py-3 px-6 text-center text-red-500">Erreur lors du chargement des livres.</td></tr>`;
    }
  }

  function displayBooks(books) {
    booksTableBody.innerHTML = ''; // Clear existing content
    if (books.length === 0) {
      booksTableBody.innerHTML = `<tr><td colspan="4" class="py-3 px-6 text-center text-gray-500">Aucun livre trouvé.</td></tr>`;
      return;
    }

    books.forEach(book => {
      const row = `
                <tr class="border-b border-gray-200 hover:bg-gray-50">
                    <td class="py-3 px-6">${book.titre}</td>
                    <td class="py-3 px-6">${book.isbn}</td>
                    <td class="py-3 px-6">${book.genre}</td>
                    <td class="py-3 px-6">${book.disponibles} / ${book.nombreExemplaires}</td>
                </tr>
            `;
      booksTableBody.innerHTML += row;
    });
  }

  async function sendChatbotMessage(message) {
    addMessageToChat('user', message);
    userMessageInput.value = ''; // Clear input

    // Add a loading indicator
    const loadingDiv = document.createElement('div');
    loadingDiv.classList.add('flex', 'justify-start', 'mb-3');
    loadingDiv.innerHTML = `
            <div class="bg-gray-300 text-gray-800 p-3 rounded-lg max-w-[75%] animate-pulse">
                Le chatbot réfléchit...
            </div>
        `;
    chatWindow.appendChild(loadingDiv);
    chatWindow.scrollTop = chatWindow.scrollHeight;

    try {
      const response = await fetch(`${backendBaseUrl}/chatbot/recommend`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ query: message }),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      chatWindow.removeChild(loadingDiv); // Remove loading indicator
      addMessageToChat('bot', data.message, data.recommendedBooks);
    } catch (error) {
      console.error('Error communicating with chatbot:', error);
      chatWindow.removeChild(loadingDiv); // Remove loading indicator
      addMessageToChat('bot', 'Désolé, une erreur est survenue. Veuillez réessayer plus tard.');
    }
  }

  // --- Event Listeners ---
  viewBooksBtn.addEventListener('click', () => {
    showSection(booksSection);
    fetchBooks();
  });

  openChatbotBtn.addEventListener('click', toggleChatbot);
  closeChatbotBtn.addEventListener('click', toggleChatbot);

  chatbotForm.addEventListener('submit', (event) => {
    event.preventDefault(); // Prevent form submission
    const message = userMessageInput.value.trim();
    if (message) {
      sendChatbotMessage(message);
    }
  });

  // Initial load
  showSection(homeSection);
});
