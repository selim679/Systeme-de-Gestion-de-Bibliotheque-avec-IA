document.addEventListener("DOMContentLoaded", () => {
  const backendBaseUrl = "/api"; // Utilisation de chemins relatifs car le frontend est servi par le backend

  // --- DOM Elements --- //
  const navTabs = document.querySelectorAll(".nav-tab");
  const tabContents = document.querySelectorAll(".tab-content");

  // Home Section Stats
  const totalBooksEl = document.getElementById("total-books");
  const availableBooksEl = document.getElementById("available-books");
  const currentLoansEl = document.getElementById("current-loans");

  // Books Section
  const booksSection = document.getElementById("books-section");
  const booksTableBody = document.getElementById("books-table-body");
  const addBookBtn = document.getElementById("add-book-btn");

  // Authors Section
  const authorsSection = document.getElementById("authors-section");
  const authorsTableBody = document.getElementById("authors-table-body");
  const addAuthorBtn = document.getElementById("add-author-btn");

  // Members Section
  const membersSection = document.getElementById("members-section");
  const membersTableBody = document.getElementById("members-table-body");
  const addMemberBtn = document.getElementById("add-member-btn");

  // Loans Section
  const loansSection = document.getElementById("loans-section");
  const loansTableBody = document.getElementById("loans-table-body");
  const addLoanBtn = document.getElementById("add-loan-btn");

  // Chatbot Section
  const chatbotSection = document.getElementById("chatbot-section");
  const chatWindow = document.getElementById("chat-window");
  const chatbotForm = document.getElementById("chatbot-form");
  const userMessageInput = document.getElementById("user-message-input");

  // CRUD Modal
  const crudModal = document.getElementById("crud-modal");
  const modalTitle = document.getElementById("modal-title");
  const crudForm = document.getElementById("crud-form");
  const formFieldsContainer = document.getElementById("form-fields-container");
  const cancelModalBtn = document.getElementById("cancel-modal-btn");
  const saveModalBtn = document.getElementById("save-modal-btn");

  let currentEntity = ""; // To keep track of which entity is being edited/added
  let currentEntityId = null; // To keep track of the ID for update operations

  // --- Utility Functions --- //
  function showSection(targetId) {
    tabContents.forEach(section => {
      section.classList.add("hidden");
    });
    document.getElementById(targetId).classList.remove("hidden");

    navTabs.forEach(tab => {
      if (tab.dataset.target === targetId) {
        tab.classList.add("text-blue-600", "font-bold");
        tab.classList.remove("text-gray-700", "font-medium");
      } else {
        tab.classList.remove("text-blue-600", "font-bold");
        tab.classList.add("text-gray-700", "font-medium");
      }
    });
  }

  function openModal(title, entityType, entityData = null) {
    crudModal.classList.remove("hidden");
    modalTitle.textContent = title;
    formFieldsContainer.innerHTML = ""; // Clear previous fields
    currentEntity = entityType;
    currentEntityId = entityData ? entityData.id : null;

    let fieldsHtml = "";
    switch (entityType) {
      case "book":
        fieldsHtml = `
                    <label for="book-titre" class="block text-gray-700 text-sm font-bold mb-2">Titre:</label>
                    <input type="text" id="book-titre" name="titre" class="form-input w-full p-2 border rounded" value="${entityData ? entityData.titre : ""}" required>

                    <label for="book-isbn" class="block text-gray-700 text-sm font-bold mb-2 mt-4">ISBN:</label>
                    <input type="text" id="book-isbn" name="isbn" class="form-input w-full p-2 border rounded" value="${entityData ? entityData.isbn : ""}" required>

                    <label for="book-datePublication" class="block text-gray-700 text-sm font-bold mb-2 mt-4">Date de Publication:</label>
                    <input type="date" id="book-datePublication" name="datePublication" class="form-input w-full p-2 border rounded" value="${entityData ? entityData.datePublication : ""}" required>

                    <label for="book-genre" class="block text-gray-700 text-sm font-bold mb-2 mt-4">Genre:</label>
                    <input type="text" id="book-genre" name="genre" class="form-input w-full p-2 border rounded" value="${entityData ? entityData.genre : ""}" required>

                    <label for="book-nombreExemplaires" class="block text-gray-700 text-sm font-bold mb-2 mt-4">Nombre d'Exemplaires:</label>
                    <input type="number" id="book-nombreExemplaires" name="nombreExemplaires" class="form-input w-full p-2 border rounded" value="${entityData ? entityData.nombreExemplaires : 0}" required>

                    <label for="book-disponibles" class="block text-gray-700 text-sm font-bold mb-2 mt-4">Exemplaires Disponibles:</label>
                    <input type="number" id="book-disponibles" name="disponibles" class="form-input w-full p-2 border rounded" value="${entityData ? entityData.disponibles : 0}" required>

                    <label for="book-authorIds" class="block text-gray-700 text-sm font-bold mb-2 mt-4">Auteurs (IDs séparés par des virgules):</label>
                    <input type="text" id="book-authorIds" name="authorIds" class="form-input w-full p-2 border rounded" value="${entityData && entityData.authors ? entityData.authors.map(a => a.id).join(", ") : ""}">
                `;
        break;
      case "author":
        fieldsHtml = `
                    <label for="author-nom" class="block text-gray-700 text-sm font-bold mb-2">Nom:</label>
                    <input type="text" id="author-nom" name="nom" class="form-input w-full p-2 border rounded" value="${entityData ? entityData.nom : ""}" required>

                    <label for="author-prenom" class="block text-gray-700 text-sm font-bold mb-2 mt-4">Prénom:</label>
                    <input type="text" id="author-prenom" name="prenom" class="form-input w-full p-2 border rounded" value="${entityData ? entityData.prenom : ""}" required>

                    <label for="author-dateNaissance" class="block text-gray-700 text-sm font-bold mb-2 mt-4">Date de Naissance:</label>
                    <input type="date" id="author-dateNaissance" name="dateNaissance" class="form-input w-full p-2 border rounded" value="${entityData ? entityData.dateNaissance : ""}" required>

                    <label for="author-nationalite" class="block text-gray-700 text-sm font-bold mb-2 mt-4">Nationalité:</label>
                    <input type="text" id="author-nationalite" name="nationalite" class="form-input w-full p-2 border rounded" value="${entityData ? entityData.nationalite : ""}" required>
                `;
        break;
      case "member":
        fieldsHtml = `
                    <label for="member-nom" class="block text-gray-700 text-sm font-bold mb-2">Nom:</label>
                    <input type="text" id="member-nom" name="nom" class="form-input w-full p-2 border rounded" value="${entityData ? entityData.nom : ""}" required>

                    <label for="member-prenom" class="block text-gray-700 text-sm font-bold mb-2 mt-4">Prénom:</label>
                    <input type="text" id="member-prenom" name="prenom" class="form-input w-full p-2 border rounded" value="${entityData ? entityData.prenom : ""}" required>

                    <label for="member-email" class="block text-gray-700 text-sm font-bold mb-2 mt-4">Email:</label>
                    <input type="email" id="member-email" name="email" class="form-input w-full p-2 border rounded" value="${entityData ? entityData.email : ""}" required>

                    <label for="member-dateAdhesion" class="block text-gray-700 text-sm font-bold mb-2 mt-4">Date d'Adhésion:</label>
                    <input type="date" id="member-dateAdhesion" name="dateAdhesion" class="form-input w-full p-2 border rounded" value="${entityData ? entityData.dateAdhesion : ""}" required>
                `;
        break;
      case "loan":
        fieldsHtml = `
                    <label for="loan-bookId" class="block text-gray-700 text-sm font-bold mb-2">ID du Livre:</label>
                    <input type="number" id="loan-bookId" name="bookId" class="form-input w-full p-2 border rounded" value="${entityData && entityData.book ? entityData.book.id : ""}" required>

                    <label for="loan-memberId" class="block text-gray-700 text-sm font-bold mb-2 mt-4">ID du Membre:</label>
                    <input type="number" id="loan-memberId" name="memberId" class="form-input w-full p-2 border rounded" value="${entityData && entityData.member ? entityData.member.id : ""}" required>

                    <label for="loan-dateEmprunt" class="block text-gray-700 text-sm font-bold mb-2 mt-4">Date d'Emprunt:</label>
                    <input type="date" id="loan-dateEmprunt" name="dateEmprunt" class="form-input w-full p-2 border rounded" value="${entityData ? entityData.dateEmprunt : ""}" required>

                    <label for="loan-dateRetourPrevue" class="block text-gray-700 text-sm font-bold mb-2 mt-4">Date Retour Prévue:</label>
                    <input type="date" id="loan-dateRetourPrevue" name="dateRetourPrevue" class="form-input w-full p-2 border rounded" value="${entityData ? entityData.dateRetourPrevue : ""}" required>

                    ${entityData && entityData.dateRetourEffective ? `
                    <label for="loan-dateRetourEffective" class="block text-gray-700 text-sm font-bold mb-2 mt-4">Date Retour Effective:</label>
                    <input type="date" id="loan-dateRetourEffective" name="dateRetourEffective" class="form-input w-full p-2 border rounded" value="${entityData.dateRetourEffective}">

                    <label for="loan-penalite" class="block text-gray-700 text-sm font-bold mb-2 mt-4">Pénalité:</label>
                    <input type="number" step="0.01" id="loan-penalite" name="penalite" class="form-input w-full p-2 border rounded" value="${entityData.penalite || 0}">
                    ` : ""}
                `;
        break;
    }
    formFieldsContainer.innerHTML = fieldsHtml;
  }

  function closeModal() {
    crudModal.classList.add("hidden");
    crudForm.reset();
  }

  function addMessageToChat(sender, text, books = []) {
    const messageDiv = document.createElement("div");
    messageDiv.classList.add("mb-3");

    const contentDiv = document.createElement("div");
    contentDiv.classList.add("p-3", "rounded-lg", "max-w-[75%]");

    if (sender === "user") {
      messageDiv.classList.add("flex", "justify-end");
      contentDiv.classList.add("bg-blue-500", "text-white");
    } else {
      messageDiv.classList.add("flex", "justify-start");
      contentDiv.classList.add("bg-gray-200", "text-gray-800");
    }

    contentDiv.innerHTML = `<p>${text}</p>`;

    if (books.length > 0) {
      const booksList = document.createElement("div");
      booksList.classList.add("mt-2", "border-t", "border-gray-400", "pt-2");
      booksList.innerHTML = "<p class=\"font-semibold\">Suggestions :</p><ul class=\"list-disc list-inside ml-5\">";
      books.forEach(book => {
        booksList.innerHTML += `<li>${book.titre} (${book.genre})</li>`;
      });
      booksList.innerHTML += "</ul>";
      contentDiv.appendChild(booksList);
    }

    messageDiv.appendChild(contentDiv);
    chatWindow.appendChild(messageDiv);
    chatWindow.scrollTop = chatWindow.scrollHeight; // Scroll to latest message
  }

  // --- API Calls & UI Rendering --- //

  // --- Dashboard Stats ---
  async function fetchDashboardStats() {
    try {
      const booksResponse = await fetch(`${backendBaseUrl}/books`);
      const books = await booksResponse.json();
      totalBooksEl.textContent = books.length;
      availableBooksEl.textContent = books.filter(b => b.disponibles > 0).length;

      const loansResponse = await fetch(`${backendBaseUrl}/loans/current`);
      const currentLoans = await loansResponse.json();
      currentLoansEl.textContent = currentLoans.length;
    } catch (error) {
      console.error("Error fetching dashboard stats:", error);
      totalBooksEl.textContent = "N/A";
      availableBooksEl.textContent = "N/A";
      currentLoansEl.textContent = "N/A";
    }
  }

  // --- Books --- //
  async function fetchBooks() {
    try {
      const response = await fetch(`${backendBaseUrl}/books`);
      if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
      const books = await response.json();
      displayBooks(books);
    } catch (error) {
      console.error("Error fetching books:", error);
      booksTableBody.innerHTML = `<tr><td colspan="5" class="py-3 px-6 text-center text-red-500">Erreur lors du chargement des livres.</td></tr>`;
    }
  }

  function displayBooks(books) {
    booksTableBody.innerHTML = "";
    if (books.length === 0) {
      booksTableBody.innerHTML = `<tr><td colspan="5" class="py-3 px-6 text-center text-gray-500">Aucun livre trouvé.</td></tr>`;
      return;
    }
    books.forEach(book => {
      const row = `
                <tr class="border-b border-gray-200 hover:bg-gray-50">
                    <td class="py-3 px-6">${book.titre}</td>
                    <td class="py-3 px-6">${book.isbn}</td>
                    <td class="py-3 px-6">${book.genre}</td>
                    <td class="py-3 px-6">${book.disponibles} / ${book.nombreExemplaires}</td>
                    <td class="py-3 px-6 flex space-x-2">
                        <button class="text-blue-600 hover:text-blue-800 font-medium edit-btn" data-entity="book" data-id="${book.id}">Éditer</button>
                        <button class="text-red-600 hover:text-red-800 font-medium delete-btn" data-entity="book" data-id="${book.id}">Supprimer</button>
                    </td>
                </tr>
            `;
      booksTableBody.innerHTML += row;
    });
  }

  async function saveBook(bookData) {
    try {
      let response;
      if (currentEntityId) {
        response = await fetch(`${backendBaseUrl}/books/${currentEntityId}`, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(bookData)
        });
      } else {
        response = await fetch(`${backendBaseUrl}/books`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(bookData)
        });
      }
      if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
      closeModal();
      fetchBooks();
      fetchDashboardStats();
    } catch (error) {
      console.error("Error saving book:", error);
      alert("Erreur lors de l'enregistrement du livre.");
    }
  }

  async function deleteBook(id) {
    if (!confirm("Êtes-vous sûr de vouloir supprimer ce livre ?")) return;
    try {
      const response = await fetch(`${backendBaseUrl}/books/${id}`, {
        method: "DELETE"
      });
      if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
      fetchBooks();
      fetchDashboardStats();
    } catch (error) {
      console.error("Error deleting book:", error);
      alert("Erreur lors de la suppression du livre.");
    }
  }

  // --- Authors --- //
  async function fetchAuthors() {
    try {
      const response = await fetch(`${backendBaseUrl}/authors`);
      if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
      const authors = await response.json();
      displayAuthors(authors);
    } catch (error) {
      console.error("Error fetching authors:", error);
      authorsTableBody.innerHTML = `<tr><td colspan="4" class="py-3 px-6 text-center text-red-500">Erreur lors du chargement des auteurs.</td></tr>`;
    }
  }

  function displayAuthors(authors) {
    authorsTableBody.innerHTML = "";
    if (authors.length === 0) {
      authorsTableBody.innerHTML = `<tr><td colspan="4" class="py-3 px-6 text-center text-gray-500">Aucun auteur trouvé.</td></tr>`;
      return;
    }
    authors.forEach(author => {
      const row = `
                <tr class="border-b border-gray-200 hover:bg-gray-50">
                    <td class="py-3 px-6">${author.nom}</td>
                    <td class="py-3 px-6">${author.prenom}</td>
                    <td class="py-3 px-6">${author.nationalite}</td>
                    <td class="py-3 px-6 flex space-x-2">
                        <button class="text-blue-600 hover:text-blue-800 font-medium edit-btn" data-entity="author" data-id="${author.id}">Éditer</button>
                        <button class="text-red-600 hover:text-red-800 font-medium delete-btn" data-entity="author" data-id="${author.id}">Supprimer</button>
                    </td>
                </tr>
            `;
      authorsTableBody.innerHTML += row;
    });
  }

  async function saveAuthor(authorData) {
    try {
      let response;
      if (currentEntityId) {
        response = await fetch(`${backendBaseUrl}/authors/${currentEntityId}`, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(authorData)
        });
      } else {
        response = await fetch(`${backendBaseUrl}/authors`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(authorData)
        });
      }
      if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
      closeModal();
      fetchAuthors();
    } catch (error) {
      console.error("Error saving author:", error);
      alert("Erreur lors de l'enregistrement de l'auteur.");
    }
  }

  async function deleteAuthor(id) {
    if (!confirm("Êtes-vous sûr de vouloir supprimer cet auteur ?")) return;
    try {
      const response = await fetch(`${backendBaseUrl}/authors/${id}`, {
        method: "DELETE"
      });
      if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
      fetchAuthors();
    } catch (error) {
      console.error("Error deleting author:", error);
      alert("Erreur lors de la suppression de l'auteur.");
    }
  }

  // --- Members --- //
  async function fetchMembers() {
    try {
      const response = await fetch(`${backendBaseUrl}/members`);
      if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
      const members = await response.json();
      displayMembers(members);
    } catch (error) {
      console.error("Error fetching members:", error);
      membersTableBody.innerHTML = `<tr><td colspan="4" class="py-3 px-6 text-center text-red-500">Erreur lors du chargement des membres.</td></tr>`;
    }
  }

  function displayMembers(members) {
    membersTableBody.innerHTML = "";
    if (members.length === 0) {
      membersTableBody.innerHTML = `<tr><td colspan="4" class="py-3 px-6 text-center text-gray-500">Aucun membre trouvé.</td></tr>`;
      return;
    }
    members.forEach(member => {
      const row = `
                <tr class="border-b border-gray-200 hover:bg-gray-50">
                    <td class="py-3 px-6">${member.nom}</td>
                    <td class="py-3 px-6">${member.prenom}</td>
                    <td class="py-3 px-6">${member.email}</td>
                    <td class="py-3 px-6 flex space-x-2">
                        <button class="text-blue-600 hover:text-blue-800 font-medium edit-btn" data-entity="member" data-id="${member.id}">Éditer</button>
                        <button class="text-red-600 hover:text-red-800 font-medium delete-btn" data-entity="member" data-id="${member.id}">Supprimer</button>
                    </td>
                </tr>
            `;
      membersTableBody.innerHTML += row;
    });
  }

  async function saveMember(memberData) {
    try {
      let response;
      if (currentEntityId) {
        response = await fetch(`${backendBaseUrl}/members/${currentEntityId}`, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(memberData)
        });
      } else {
        response = await fetch(`${backendBaseUrl}/members`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(memberData)
        });
      }
      if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
      closeModal();
      fetchMembers();
    } catch (error) {
      console.error("Error saving member:", error);
      alert("Erreur lors de l'enregistrement du membre.");
    }
  }

  async function deleteMember(id) {
    if (!confirm("Êtes-vous sûr de vouloir supprimer ce membre ?")) return;
    try {
      const response = await fetch(`${backendBaseUrl}/members/${id}`, {
        method: "DELETE"
      });
      if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
      fetchMembers();
    } catch (error) {
      console.error("Error deleting member:", error);
      alert("Erreur lors de la suppression du membre.");
    }
  }

  // --- Loans --- //
  async function fetchLoans() {
    try {
      const response = await fetch(`${backendBaseUrl}/loans`);
      if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
      const loans = await response.json();
      displayLoans(loans);
    } catch (error) {
      console.error("Error fetching loans:", error);
      loansTableBody.innerHTML = `<tr><td colspan="6" class="py-3 px-6 text-center text-red-500">Erreur lors du chargement des emprunts.</td></tr>`;
    }
  }

  function displayLoans(loans) {
    loansTableBody.innerHTML = "";
    if (loans.length === 0) {
      loansTableBody.innerHTML = `<tr><td colspan="6" class="py-3 px-6 text-center text-gray-500">Aucun emprunt trouvé.</td></tr>`;
      return;
    }
    loans.forEach(loan => {
      const status = loan.dateRetourEffective ? "Retourné" : (new Date(loan.dateRetourPrevue) < new Date() ? "En Retard" : "En Cours");
      const statusClass = loan.dateRetourEffective ? "text-green-600" : (new Date(loan.dateRetourPrevue) < new Date() ? "text-red-600" : "text-yellow-600");
      const row = `
                <tr class="border-b border-gray-200 hover:bg-gray-50">
                    <td class="py-3 px-6">${loan.book ? loan.book.titre : "N/A"}</td>
                    <td class="py-3 px-6">${loan.member ? loan.member.nom + " " + loan.member.prenom : "N/A"}</td>
                    <td class="py-3 px-6">${loan.dateEmprunt}</td>
                    <td class="py-3 px-6">${loan.dateRetourPrevue}</td>
                    <td class="py-3 px-6 ${statusClass}">${status}</td>
                    <td class="py-3 px-6 flex space-x-2">
                        ${!loan.dateRetourEffective ? `<button class="text-green-600 hover:text-green-800 font-medium return-loan-btn" data-id="${loan.id}">Retourner</button>` : ""}
                        <button class="text-blue-600 hover:text-blue-800 font-medium edit-btn" data-entity="loan" data-id="${loan.id}">Éditer</button>
                        <button class="text-red-600 hover:text-red-800 font-medium delete-btn" data-entity="loan" data-id="${loan.id}">Supprimer</button>
                    </td>
                </tr>
            `;
      loansTableBody.innerHTML += row;
    });
  }

  async function saveLoan(loanData) {
    try {
      let response;
      if (currentEntityId) {
        response = await fetch(`${backendBaseUrl}/loans/${currentEntityId}`, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(loanData)
        });
      } else {
        response = await fetch(`${backendBaseUrl}/loans`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(loanData)
        });
      }
      if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
      closeModal();
      fetchLoans();
      fetchDashboardStats();
    } catch (error) {
      console.error("Error saving loan:", error);
      alert("Erreur lors de l'enregistrement de l'emprunt.");
    }
  }

  async function returnLoan(id) {
    if (!confirm("Confirmer le retour de ce livre ?")) return;
    try {
      const response = await fetch(`${backendBaseUrl}/loans/${id}/return`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ dateRetourEffective: new Date().toISOString().split("T")[0] })
      });
      if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
      fetchLoans();
      fetchDashboardStats();
    } catch (error) {
      console.error("Error returning loan:", error);
      alert("Erreur lors de l'enregistrement du retour.");
    }
  }

  async function deleteLoan(id) {
    if (!confirm("Êtes-vous sûr de vouloir supprimer cet emprunt ?")) return;
    try {
      const response = await fetch(`${backendBaseUrl}/loans/${id}`, {
        method: "DELETE"
      });
      if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
      fetchLoans();
      fetchDashboardStats();
    } catch (error) {
      console.error("Error deleting loan:", error);
      alert("Erreur lors de la suppression de l'emprunt.");
    }
  }

  // --- Chatbot --- //
  async function sendChatbotMessage(message) {
    addMessageToChat("user", message);
    userMessageInput.value = "";

    const loadingDiv = document.createElement("div");
    loadingDiv.classList.add("flex", "justify-start", "mb-3");
    loadingDiv.innerHTML = `
            <div class="bg-gray-200 text-gray-800 p-3 rounded-lg max-w-[75%] animate-pulse">
                Le chatbot réfléchit...
            </div>
        `;
    chatWindow.appendChild(loadingDiv);
    chatWindow.scrollTop = chatWindow.scrollHeight;

    try {
      const response = await fetch(`${backendBaseUrl}/chatbot/recommend`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ query: message }),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(`HTTP error! status: ${response.status}, message: ${errorData.message || JSON.stringify(errorData)}`);
      }

      const data = await response.json();
      chatWindow.removeChild(loadingDiv); // Remove loading indicator
      addMessageToChat("bot", data.message, data.recommendedBooks);
    } catch (error) {
      console.error("Error communicating with chatbot:", error);
      chatWindow.removeChild(loadingDiv);
      addMessageToChat("bot", `Désolé, une erreur est survenue: ${error.message}. Veuillez réessayer plus tard.`);
    }
  }

  // --- Event Listeners --- //

  // Tab Navigation
  navTabs.forEach(tab => {
    tab.addEventListener("click", () => {
      const targetId = tab.dataset.target;
      showSection(targetId);
      // Fetch data when switching tabs
      if (targetId === "books-section") fetchBooks();
      if (targetId === "authors-section") fetchAuthors();
      if (targetId === "members-section") fetchMembers();
      if (targetId === "loans-section") fetchLoans();
      if (targetId === "home-section") fetchDashboardStats();
    });
  });

  // CRUD Buttons
  addBookBtn.addEventListener("click", () => openModal("Ajouter un Livre", "book"));
  addAuthorBtn.addEventListener("click", () => openModal("Ajouter un Auteur", "author"));
  addMemberBtn.addEventListener("click", () => openModal("Ajouter un Membre", "member"));
  addLoanBtn.addEventListener("click", () => openModal("Enregistrer un Emprunt", "loan"));

  // Event delegation for edit and delete buttons (since they are dynamically added)
  document.addEventListener("click", async (event) => {
    if (event.target.classList.contains("edit-btn")) {
      const entity = event.target.dataset.entity;
      const id = event.target.dataset.id;
      try {
        const response = await fetch(`${backendBaseUrl}/${entity}s/${id}`);
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        const data = await response.json();
        openModal(`Éditer ${entity === "book" ? "le Livre" : entity === "author" ? "l'Auteur" : entity === "member" ? "le Membre" : "l'Emprunt"}`, entity, data);
      } catch (error) {
        console.error(`Error fetching ${entity}:`, error);
        alert(`Erreur lors du chargement des données de l'${entity}.`);
      }
    } else if (event.target.classList.contains("delete-btn")) {
      const entity = event.target.dataset.entity;
      const id = event.target.dataset.id;
      if (entity === "book") deleteBook(id);
      else if (entity === "author") deleteAuthor(id);
      else if (entity === "member") deleteMember(id);
      else if (entity === "loan") deleteLoan(id);
    } else if (event.target.classList.contains("return-loan-btn")) {
      const id = event.target.dataset.id;
      returnLoan(id);
    }
  });

  // Modal Form Submission
  crudForm.addEventListener("submit", (event) => {
    event.preventDefault();
    const formData = new FormData(crudForm);
    const data = {};
    for (let [key, value] of formData.entries()) {
      if (key === "authorIds") {
        data[key] = value.split(",").map(id => parseInt(id.trim())).filter(id => !isNaN(id));
      } else if (["nombreExemplaires", "disponibles", "bookId", "memberId", "penalite"].includes(key)) {
        data[key] = parseFloat(value);
      } else {
        data[key] = value;
      }
    }

    // Special handling for loan return date if present in edit mode
    if (currentEntity === "loan" && currentEntityId && data.dateRetourEffective) {
      // The return is handled by a separate endpoint, so we just update the loan itself
      // This part might need refinement depending on how your backend handles loan updates vs returns
      // For simplicity, we'll just save the loan data as is.
    }

    if (currentEntity === "book") saveBook(data);
    else if (currentEntity === "author") saveAuthor(data);
    else if (currentEntity === "member") saveMember(data);
    else if (currentEntity === "loan") saveLoan(data);
  });

  // Close Modal
  cancelModalBtn.addEventListener("click", closeModal);
  crudModal.addEventListener("click", (event) => {
    if (event.target === crudModal) closeModal(); // Close if clicking outside the modal content
  });

  // Chatbot Form Submission
  chatbotForm.addEventListener("submit", (event) => {
    event.preventDefault();
    const message = userMessageInput.value.trim();
    if (message) {
      sendChatbotMessage(message);
    }
  });

  // Initial Load
  showSection("home-section");
  fetchDashboardStats();
});
