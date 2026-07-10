document.addEventListener('DOMContentLoaded', () => {
    // Form and input selectors
    const tabLogin = document.getElementById('tab-login');
    const tabRegister = document.getElementById('tab-register');
    const loginForm = document.getElementById('login-form');
    const registerForm = document.getElementById('register-form');
    const messageArea = document.getElementById('message-area');
    const rosterCountSpan = document.getElementById('roster-count');
    const rosterTbody = document.getElementById('roster-tbody');

    // Register form inputs
    const regNameInput = document.getElementById('reg-name');
    const regIdInput = document.getElementById('reg-id');
    const regDobInput = document.getElementById('reg-dob');
    const regPasswordInput = document.getElementById('reg-password');

    // Login form inputs
    const loginIdInput = document.getElementById('login-id');
    const loginPasswordInput = document.getElementById('login-password');

    // Handle tab switching
    tabLogin.addEventListener('click', () => {
        tabLogin.classList.add('active');
        tabRegister.classList.remove('active');
        loginForm.classList.add('active');
        registerForm.classList.remove('active');
        clearMessage();
    });

    tabRegister.addEventListener('click', () => {
        tabRegister.classList.add('active');
        tabLogin.classList.remove('active');
        registerForm.classList.add('active');
        loginForm.classList.remove('active');
        clearMessage();
    });

    // Auto-format DOB input as MM/DD/YY or MM/DD/YYYY
    regDobInput.addEventListener('input', (e) => {
        let value = e.target.value.replace(/\D/g, ''); // Keep only digits
        let formatted = '';

        if (value.length > 0) {
            formatted = value.substring(0, 2);
            if (value.length > 2) {
                formatted += '/' + value.substring(2, 4);
                if (value.length > 4) {
                    formatted += '/' + value.substring(4, 8); // Limit year to 4 digits max
                }
            }
        }
        e.target.value = formatted;
    });

    // Parse MM/DD/YY or MM/DD/YYYY to YYYY-MM-DD
    function parseDateToIso(dateStr) {
        const parts = dateStr.split('/');
        if (parts.length !== 3) return null;

        const month = parts[0];
        const day = parts[1];
        let year = parts[2];

        const m = parseInt(month, 10);
        const d = parseInt(day, 10);
        if (isNaN(m) || m < 1 || m > 12 || isNaN(d) || d < 1 || d > 31) {
            return null;
        }

        // Expand 2-digit years
        if (year.length === 2) {
            const currentYear = new Date().getFullYear();
            const currentCentury = Math.floor(currentYear / 100) * 100;
            const yearTwoDigit = currentYear % 100;
            const yrInt = parseInt(year, 10);

            if (yrInt > yearTwoDigit + 5) {
                year = String(currentCentury - 100 + yrInt);
            } else {
                year = String(currentCentury + yrInt);
            }
        } else if (year.length !== 4) {
            return null;
        }

        return `${year}-${month.padStart(2, '0')}-${day.padStart(2, '0')}`;
    }

    // Refresh Roster Table and Total Counter
    async function refreshRoster() {
        try {
            const response = await fetch('/students');
            if (response.ok) {
                const students = await response.json();
                
                // Update total count
                rosterCountSpan.textContent = students.length;

                // Populate table
                if (students.length === 0) {
                    rosterTbody.innerHTML = `
                        <tr>
                            <td colspan="4" class="empty-state">No students registered yet.</td>
                        </tr>
                    `;
                } else {
                    rosterTbody.innerHTML = students.map(student => {
                        // Display the studentId as the default password for convenience
                        return `
                            <tr>
                                <td>${escapeHtml(student.studentName)}</td>
                                <td><code>${escapeHtml(student.studentId)}</code></td>
                                <td>${student.dateOfBirth}</td>
                                <td><code>${escapeHtml(student.studentId)}</code> <span style="font-size:0.8rem; color:#666;">(or custom)</span></td>
                            </tr>
                        `;
                    }).join('');
                }
            }
        } catch (error) {
            console.error('Error fetching roster:', error);
        }
    }

    // Handle registration submission
    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        clearMessage();

        const name = regNameInput.value.trim();
        const id = regIdInput.value.trim();
        const rawDob = regDobInput.value.trim();
        const password = regPasswordInput.value; // Optional password

        const formattedDob = parseDateToIso(rawDob);
        if (!formattedDob) {
            showMessage('Error: Please enter a valid Date of Birth in MM/DD/YY or MM/DD/YYYY format.', 'error');
            return;
        }

        try {
            const params = new URLSearchParams();
            params.append('name', name);
            params.append('id', id);
            params.append('dob', formattedDob);
            if (password) {
                params.append('password', password);
            }

            const response = await fetch('/student', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: params
            });

            const resultText = await response.text();

            if (response.ok && !resultText.startsWith('Error:')) {
                showMessage(resultText, 'success');
                registerForm.reset();
                refreshRoster();
            } else {
                showMessage(resultText, 'error');
            }
        } catch (error) {
            showMessage('Error: Failed to register. Please check database connection.', 'error');
            console.error('Registration error:', error);
        }
    });

    // Handle login submission
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        clearMessage();

        const id = loginIdInput.value.trim();
        const password = loginPasswordInput.value;

        try {
            const params = new URLSearchParams();
            params.append('id', id);
            params.append('password', password);

            const response = await fetch('/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: params
            });

            const resultText = await response.text();

            if (response.ok && !resultText.startsWith('Error:')) {
                showMessage(resultText, 'success');
                loginForm.reset();
            } else {
                showMessage(resultText, 'error');
            }
        } catch (error) {
            showMessage('Error: Failed to connect to server.', 'error');
            console.error('Login error:', error);
        }
    });

    function showMessage(text, type) {
        messageArea.textContent = text;
        messageArea.className = `message-container ${type}`;
        messageArea.style.display = 'block';
    }

    function clearMessage() {
        messageArea.className = 'message-container';
        messageArea.style.display = 'none';
    }

    function escapeHtml(str) {
        return str
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }

    // Initial load
    refreshRoster();
});
